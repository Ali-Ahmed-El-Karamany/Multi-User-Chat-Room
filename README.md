# Java Multi-User Chat Room

Simple multi-threaded TCP chat application implemented using Java.


## Project Overview

This repository contains a simple multi-client chat application implemented using Java `ServerSocket` and `Socket` APIs.  
The server accepts multiple client connections concurrently and broadcasts messages from any client to all other connected clients.
The project demonstrates socket programming and multi-threading.

## Architecture

This project follows a classical client-server (TCP) architecture:

- **Server**
    - Listens on a TCP port |`8888`| using `ServerSocket`.
    - For each incoming connection it creates a `ClientHandler` and runs it in a new thread.
    - Responsible for accepting connections and clean shutdown of the server socket.

- **ClientHandler**
    - **Manages an individual client connection:** reading messages from that client and broadcasting to other connected clients.
    - Maintains a static list of `clientHandlers` representing active clients.

- **Client**
    - Connects to the server, sends a username as the first message, listens for incoming server broadcasts on a background thread, and reads user input from the console to send messages.

## Features

- **Multi-client support:** concurrent clients handled using a separate thread for each client.
- **Broadcast:** messages sent from one client are broadcast to all other connected clients.
- **Graceful disconnect:** client can send `exit` to disconnect, server notifies remaining clients.
- **Basic logging:** uses `java.util.logging` for info/warning/error messages.


## How to Run the Project

1. **Compile** the Java files (from project root):
   ```bash
   javac -d out src/*.java
   ```

2. **Run the Server** (default port `8888`):
   ```bash
   java -cp out Server
   ```

3. **Run a Client** (connects to `localhost:8888`):
   ```bash
   java -cp out Client
   ```

4. **Usage**
    - When the client starts it will prompt: `Define your username:` — type a username and press Enter.
    - Type chat messages and press Enter to send.
    - Type `exit` to disconnect safely.