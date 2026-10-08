#  URL Shortener

A Java-based URL Shortener application that converts long URLs into short, easy-to-share links. It includes a local HTTP server for URL redirection and click tracking.

##  Project Overview

This project is a console-based URL Shortener developed using Java.

The application generates a unique 6-character Short ID for a long URL. The URL mapping is stored locally, and the built-in HTTP server redirects users from the short URL to the original URL.

##  Features

- Generate random 6-character Short IDs
- Shorten long HTTP/HTTPS URLs
- Validate URL input
- Store URL data using local file storage
- Built-in HTTP server
- Redirect short URLs to original URLs
- Track click counts
- Find original URLs using Short IDs
- Display URL statistics
- Prevent Short ID collisions

##  Technologies Used

- Java
- HashMap
- File I/O
- Java HTTP Server
- Scanner

##  Project Structure

```text
URL-Shortener/
│
├── src/
│   └── URLShortener.java
│
├── data/
│   └── url.txt
│
├── README.md
├── .gitignore
└── LICENSE
