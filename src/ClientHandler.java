import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles communication between the server and connected client.
 * Each client is managed by its own ClientHandler running in a separate thread.
 */
public class ClientHandler implements Runnable{
    // Logger for diagnostic and status messages
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    // List of all active clients connected to the server
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    // Socket and I/O streams for the client
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    // Username sent by the client when it is connected
    private String clientUsername;

    /**
     * Initializes the handler for a newly connected client.
     * Sets up I/O streams and notifies all other clients.
     *
     * @param socket The socket representing the client connection to the server.
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;

            // Set up input/output streams for text communication
            this.reader = new BufferedReader (new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Read the first line sent from the client
            this.clientUsername = reader.readLine();

            // Add this client handler the list
            clientHandlers.add(this);

            //Notify all connected clients that a new user has joined the chat
            broadCastMSG("Server: " + clientUsername + " has entered the chat");
            logger.log(Level.INFO, "Client connected successfully: " + clientUsername);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Failed to initialize client handler for the socket",e);
            closeSocketResources();
        }
    }

    /**
     * Listens for incoming messages and broadcasts them to all other clients.
     * Handles client disconnection gracefully, if "exit" command sent.
     */
    @Override
    public void run(){
        String message;

        try {
            // Continuously read messages while the connection is active
            while((message = reader.readLine()) != null) {
                // Handles client disconnection request
                if(message.equalsIgnoreCase("exit")) {
                    logger.log(Level.INFO, clientUsername + ": Requested to disconnect");
                    break;
                }
                broadCastMSG(message);
            }

        }catch(IOException e) {
            logger.log(Level.WARNING,"Connection Lost with client: "+ clientUsername, e);
        }finally {
            // close the socket and I/O streams then remove client reference
            closeSocketResources();
        }
    }

    /**
     * Sends the given message to the other connected clients.
     *
     * @param message The message to broadcast.
     */
    public void broadCastMSG(String message) {
        for(ClientHandler clientHandler : clientHandlers) {
            try {
                // Avoid sending the message to the sender back
                if(!clientHandler.clientUsername.equals(clientUsername))
                {
                    clientHandler.writer.write(message);
                    clientHandler.writer.newLine();
                    clientHandler.writer.flush();
                }
            } catch (IOException e) {
                logger.log(Level.WARNING,"Failed to send message to the connected clients. " + "from " + clientUsername, e);
                // close the socket and I/O streams then remove client reference
                closeSocketResources();
            }
        }
    }

    /**
     * Removes this client from the active client list and notifies others.
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);

        broadCastMSG("Server: " + clientUsername + " left the chat.");
        logger.log(Level.INFO,"Client removed from the list: " + clientUsername);
    }

    /**
     * Removes the client from the list.
     * Closes all I/O streams and the socket associated with this client.
     */
    public void closeSocketResources() {
        removeClientHandler();

        try{
            if(reader != null) {
                reader.close();
            }
            if(writer != null) {
                writer.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing resources for client: " + clientUsername, e);
        }
    }
}
