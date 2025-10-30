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
                logger.info("Waiting for client...");

                // Block and wait for incoming client connection
                Socket socket = serverSocket.accept();
                logger.info("A new client has connected!");

                // Create a new ClientHandler for incoming client in a separate thread
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            // Log the exception details
            logger.log(Level.SEVERE,"Error while accepting client connection", e);
        }finally{
            // close the server socket
            closeServerSocket();
        }
    }

    /**
     * Closes the server socket and releases the associated port.
     */
    public void closeServerSocket() {
        // Check if there is actual socket object before closing the socket
        if(serverSocket != null)
        {
            try {
                logger.log(Level.INFO,"Closing server socket.");
                serverSocket.close();
                logger.log(Level.INFO,"Server stopped successfully.");
            } catch (IOException e) {
                logger.log(Level.WARNING,"Error While closing server socket", e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Server server = new Server(serverSocket);
            logger.info("Server started.");
            server.startServer();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while starting the server.",e);
        }
    }
}


