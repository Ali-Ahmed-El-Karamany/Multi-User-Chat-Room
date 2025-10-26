import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A multi-threaded server that accepts and handles multiple client connections concurrently.
 * Each client connection is assigned to a separate ClientHandler running in its own thread.
 */
public class Server {
    // Logger instance for recording server events and activities
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    /** The server socket that listens for incoming client connections */
    private final ServerSocket serverSocket;

    /**
     * Constructs a new Server with the specified ServerSocket.
     *
     * @param serverSocket the ServerSocket to use for listening to client connections
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Starts the server and continuously listens for client connections.
     * The server runs continuously, creating a new thread for each
     * connected client to enable concurrent handling of multiple clients.
     */
    public void startServer() {
        try {
            // Keep listening for new clients until the server socket is closed
            while (!serverSocket.isClosed()) {
                logger.info("Server started. Waiting for client...");

                // Block and wait for incoming client connection
                Socket socket = serverSocket.accept();
                logger.info("A new client has connected!");

                // Create a new ClientHandler for incoming client in a separate thread
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            // Log the exception details and continue or stop the server depending on the severity
            logger.log(Level.SEVERE,"Error accepting client connection", e);

            // close the server socket
            }
    }
}

