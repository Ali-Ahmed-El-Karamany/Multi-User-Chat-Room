import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the client that connects to a server using TCP sockets.
 * Handles sending messages and receiving broadcast messages from the server.
 */
public class Client {
    // Logger for diagnostic and status messages
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    // TCP socket and I/O character streams.
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    // The username for connected client.
    private String username;

    /**
     * Initializes the client connection and sets up input/output streams.
     *
     * @param socket   The socket that connects this client to the server.
     * @param username The username chosen by the client.
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        }catch(IOException e) {
            logger.log(Level.SEVERE,"Failed to initialize client.",e);
            closeSocketResources();
        }
    }

    /**
     * Reads user input from the console and sends messages to the server.
     */
    public void sendMessage() {
        try(Scanner scanner = new Scanner(System.in)) {
            // Send client username to the server
            writer.write(username);
            writer.newLine();
            writer.flush();

            System.out.println("To exit the chat room type 'exit'.");

            // listen for user input and send messages
            while(!socket.isClosed()) {
                String message = scanner.nextLine();

                // Check if the client wants to leave the chat
                if(message.equalsIgnoreCase("exit")) {
                    // Notify the server about the disconnection request
                    writer.write(message);
                    writer.newLine();
                    writer.flush();

                    System.out.println("Disconnecting from server.");
                    break;
                }

                writer.write(username + ": " + message);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING,username + ": Failed to send Message.",e);
        }finally {
            // close the socket and I/O streams then remove client reference
            closeSocketResources();
        }
    }

    /**
     * Listens for incoming messages from the server in a separate thread.
     * Prints all received messages to the client console.
     */
    public void listenForMessage() {
        new Thread(() -> {
            String message;

            try {
                // Reads messages from the server continuously
                while((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                logger.log(Level.WARNING,username + ": Connection Lost.", e);
            }finally {
                // close the socket and I/O streams then remove client reference
                closeSocketResources();
            }
        }).start(); // Start the thread immediately
    }

    /**
     * Closes all I/O streams and the socket associated with this client.
     */
    public void closeSocketResources() {
        try {
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
            logger.log(Level.WARNING, "Error closing resources for client.", e);
        }
    }


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Define your username: ");
        String username= scanner.nextLine();

        // Connect to the chat server running on localhost:8888
        Socket socket = new Socket("localhost",8888);
        Client client = new Client(socket, username);

        // Start listening for messages from the server
        client.listenForMessage();

        // Start reading user input to send messages
        client.sendMessage();
    }
}