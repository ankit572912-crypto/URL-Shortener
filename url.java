import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Scanner;

public class url {

    // ==========================================
    // Generate Random 6 Character Short ID
    // ==========================================
    public static String generateShortId() {

        String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        String shortId = "";

        for (int i = 0; i < 6; i++) {

            int index =
                    (int) (Math.random() * characters.length());

            shortId =
                    shortId + characters.charAt(index);
        }

        return shortId;
    }


    // ==========================================
    // Save One New URL
    // ==========================================
    public static void saveUrl(
            String shortId,
            String longUrl,
            int clicks) {

        try {

            FileWriter writer =
                    new FileWriter("url.txt", true);

            writer.write(
                    shortId + "|" +
                    longUrl + "|" +
                    clicks + "\n"
            );

            writer.close();

        } catch (IOException e) {

            System.out.println(
                    "Error saving URL!"
            );
        }
    }


    // ==========================================
    // Load URLs From File
    // ==========================================
    public static void loadUrls(
            HashMap<String, String> urlMap,
            HashMap<String, Integer> clickMap) {

        try {

            FileReader fileReader =
                    new FileReader("url.txt");

            BufferedReader reader =
                    new BufferedReader(fileReader);

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data =
                        line.split("\\|");

                if (data.length == 3) {

                    String shortId =
                            data[0].trim();

                    String longUrl =
                            data[1].trim();

                    int clicks =
                            Integer.parseInt(
                                    data[2].trim()
                            );

                    urlMap.put(
                            shortId,
                            longUrl
                    );

                    clickMap.put(
                            shortId,
                            clicks
                    );
                }
            }

            reader.close();

        } catch (IOException e) {

            // File does not exist yet

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid data in url.txt"
            );
        }
    }


    // ==========================================
    // Save All URLs Again
    // Used for Updating Click Count
    // ==========================================
    public static void saveAllUrls(
            HashMap<String, String> urlMap,
            HashMap<String, Integer> clickMap) {

        try {

            FileWriter writer =
                    new FileWriter("url.txt");

            for (String shortId : urlMap.keySet()) {

                String longUrl =
                        urlMap.get(shortId);

                int clicks =
                        clickMap.get(shortId);

                writer.write(
                        shortId + "|" +
                        longUrl + "|" +
                        clicks + "\n"
                );
            }

            writer.close();

        } catch (IOException e) {

            System.out.println(
                    "Error updating url.txt!"
            );
        }
    }


    // ==========================================
    // Start HTTP Server
    // ==========================================
    public static void startServer(
            HashMap<String, String> urlMap,
            HashMap<String, Integer> clickMap) {

        try {

            HttpServer server =
                    HttpServer.create(
                            new InetSocketAddress(8080),
                            0
                    );


            server.createContext(
                    "/",
                    (HttpExchange exchange) -> {

                        // Example:
                        // /UbEXTz

                        String path =
                                exchange
                                .getRequestURI()
                                .getPath();


                        // Remove "/" from beginning
                        String shortId =
                                path.substring(1);


                        // Ignore empty request
                        if (shortId.isEmpty()) {

                            String response =
                                    "URL Shortener Server is Running!";

                            exchange.sendResponseHeaders(
                                    200,
                                    response.length()
                            );

                            OutputStream os =
                                    exchange.getResponseBody();

                            os.write(
                                    response.getBytes()
                            );

                            os.close();

                            return;
                        }


                        // ==================================
                        // Check Short ID
                        // ==================================
                        if (urlMap.containsKey(shortId)) {

                            String originalUrl =
                                    urlMap.get(shortId);


                            // ==================================
                            // Increase Click Count
                            // ==================================

                            int clicks =
                                    clickMap.getOrDefault(
                                            shortId,
                                            0
                                    );

                            clicks++;

                            clickMap.put(
                                    shortId,
                                    clicks
                            );


                            // Save updated click count
                            saveAllUrls(
                                    urlMap,
                                    clickMap
                            );


                            // ==================================
                            // Redirect to Original URL
                            // ==================================

                            exchange.getResponseHeaders()
                                    .set(
                                            "Location",
                                            originalUrl
                                    );


                            exchange.sendResponseHeaders(
                                    302,
                                    -1
                            );

                        } else {

                            // ==================================
                            // Short ID Not Found
                            // ==================================

                            String response =
                                    "Short URL not found!";

                            exchange.sendResponseHeaders(
                                    404,
                                    response.length()
                            );

                            OutputStream os =
                                    exchange.getResponseBody();

                            os.write(
                                    response.getBytes()
                            );

                            os.close();
                        }


                        exchange.close();
                    }
            );


            server.setExecutor(null);

            server.start();


            System.out.println();
            System.out.println(
                    "================================"
            );

            System.out.println(
                    " Server started successfully!"
            );

            System.out.println(
                    " http://localhost:8080"
            );

            System.out.println(
                    "================================"
            );

        } catch (IOException e) {

            System.out.println(
                    "Server error: " +
                    e.getMessage()
            );
        }
    }


    // ==========================================
    // MAIN METHOD
    // ==========================================
    public static void main(String[] args) {


        // ==========================================
        // HashMaps
        // ==========================================

        HashMap<String, String> urlMap =
                new HashMap<>();

        HashMap<String, Integer> clickMap =
                new HashMap<>();


        // ==========================================
        // Load Previous URLs
        // ==========================================

        loadUrls(
                urlMap,
                clickMap
        );


        // ==========================================
        // Start Server
        // ==========================================

        startServer(
                urlMap,
                clickMap
        );


        Scanner sc =
                new Scanner(System.in);


        // ==========================================
        // MENU
        // ==========================================

        while (true) {

            System.out.println();

            System.out.println(
                    "===== URL SHORTENER ====="
            );

            System.out.println(
                    "1. Shorten URL"
            );

            System.out.println(
                    "2. Find Original URL"
            );

            System.out.println(
                    "3. Show Statistics"
            );

            System.out.println(
                    "4. Exit"
            );


            System.out.print(
                    "Enter choice: "
            );


            String input =
                    sc.nextLine();


            int choice;


            // ==========================================
            // Convert Input to Integer
            // ==========================================

            try {

                choice =
                        Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter 1, 2, 3 or 4!"
                );

                continue;
            }


            // ==========================================
            // OPTION 1
            // Shorten URL
            // ==========================================

            if (choice == 1) {

                System.out.print(
                        "Enter Long URL: "
                );

                String longUrl =
                        sc.nextLine().trim();


                // ==================================
                // URL Validation
                // ==================================

                if (!longUrl.startsWith("http://")
                        && !longUrl.startsWith("https://")) {

                    System.out.println(
                            "Invalid URL!"
                    );

                    continue;
                }


                // ==================================
                // Generate Short ID
                // ==================================

                String shortId =
                        generateShortId();


                // ==================================
                // Check Collision
                // ==================================

                while (urlMap.containsKey(shortId)) {

                    shortId =
                            generateShortId();
                }


                // ==================================
                // Store URL
                // ==================================

                urlMap.put(
                        shortId,
                        longUrl
                );

                clickMap.put(
                        shortId,
                        0
                );


                // ==================================
                // Save to File
                // ==================================

                saveUrl(
                        shortId,
                        longUrl,
                        0
                );


                // ==================================
                // Output
                // ==================================

                System.out.println();

                System.out.println(
                        "URL saved successfully!"
                );

                System.out.println(
                        "Short ID: " +
                        shortId
                );

                System.out.println(
                        "Short Link: http://localhost:8080/"
                        + shortId
                );
            }


            // ==========================================
            // OPTION 2
            // Find Original URL
            // ==========================================

            else if (choice == 2) {

                System.out.print(
                        "Enter Short ID: "
                );

                String inputId =
                        sc.nextLine().trim();


                String originalUrl =
                        urlMap.get(inputId);


                if (originalUrl != null) {

                    int clicks =
                            clickMap.getOrDefault(
                                    inputId,
                                    0
                            );

                    clicks++;


                    clickMap.put(
                            inputId,
                            clicks
                    );


                    saveAllUrls(
                            urlMap,
                            clickMap
                    );


                    System.out.println();

                    System.out.println(
                            "Original URL: " +
                            originalUrl
                    );

                    System.out.println(
                            "Clicks: " +
                            clicks
                    );

                } else {

                    System.out.println(
                            "Short ID not found!"
                    );
                }
            }


            // ==========================================
            // OPTION 3
            // Statistics
            // ==========================================

            else if (choice == 3) {

                if (urlMap.isEmpty()) {

                    System.out.println(
                            "No URLs available!"
                    );

                } else {

                    System.out.println();

                    System.out.println(
                            "===== URL STATISTICS ====="
                    );


                    for (String shortId :
                            urlMap.keySet()) {


                        String originalUrl =
                                urlMap.get(shortId);


                        int clicks =
                                clickMap.getOrDefault(
                                        shortId,
                                        0
                                );


                        System.out.println();

                        System.out.println(
                                "Short ID: " +
                                shortId
                        );

                        System.out.println(
                                "Original URL: " +
                                originalUrl
                        );

                        System.out.println(
                                "Short Link: http://localhost:8080/"
                                + shortId
                        );

                        System.out.println(
                                "Clicks: " +
                                clicks
                        );

                        System.out.println(
                                "--------------------------"
                        );
                    }
                }
            }


            // OPTION 4
            // Exit
            

            else if (choice == 4) {

                System.out.println();

                System.out.println(
                        "Server is still running."
                );

                System.out.println(
                        "Stop it using Ctrl + C."
                );

                break;
            }


           
            // INVALID OPTION
            

            else {

                System.out.println(
                        "Invalid choice!"
                );
            }
        }


        sc.close();
    }
}