//==============
//  Server Code
//==============
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    // ArrayList to track and handle clients
    static ArrayList<ClientHandler> clientsList = new ArrayList<>();

    public static void main(String[] args) {
        int port = 1234; // Define the port on which the server will listen

        // Create a server socket, binds it to the port, and starts listening.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("--------------[ Server ]--------------");
            System.out.println("Server listening on port " + port + "...");

            while (true) {
                // Accept incoming client connections
                Socket socket = serverSocket.accept();
                System.out.println("[+] A client connected: " + socket.getInetAddress());
                
                // Create a client handler for the connected client
                ClientHandler client = new ClientHandler(socket);
                
                // Start a new thread for the connected client
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Client sends a broadcast message to all other clients
    public static synchronized void sendMessageToAll(ClientHandler sender, String message) {
        for (ClientHandler client : clientsList) {
            if (client != sender)
                client.sendMessage(message);
        }
    }

    // Check if client name is already taken
    public static synchronized boolean isNameTaken(String name) {
        for (ClientHandler client : clientsList) {
            if (name.equalsIgnoreCase(client.getClientName()))
                return true;
        }
        return false;
    }

    // Add client to the clients list 
    public static synchronized void addClient(ClientHandler client) {
        clientsList.add(client);
    }

    // Remove client from the clients list
    public static synchronized void removeClient(ClientHandler client) {
        clientsList.remove(client);
    }

    // Inner class to handle each client connection
    static class ClientHandler extends Thread {
        // Attributes
        private Socket socket;
        private PrintWriter output;
        private BufferedReader input;
        private String clientName;

        // Parametrized Constructer
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        // Client Name Getter
        public String getClientName() {
            return clientName;
        }

        // Client sends message to Server
        public void sendMessage(String message) {
            output.println(message);
        }

        // Display a simple chat window when client join chat
        public void showChatWindow() {
            output.println("___________________________________");
            output.println("|                                 |");
            output.println("|           Chat Window           |");
            output.println("|                                 |");
            output.println("|---------------------------------|");
            output.println("| NOTE: type 'exit' to leave chat |");
            output.println("|_________________________________|\n");
        }

        // Display a join notification message to client, server, and other clients
        public void showJoinNotification(String clientName) {
            output.println("You joined the chat");
            System.out.println("[" + clientName + "] joined the chat");
            sendMessageToAll(this, "[" + clientName + "] joined the chat");
        }
        
        // Display a leave notification message to client, server, and other clients
        public void showLeaveNotification(String clientName) {
            output.println("You left the chat");
            System.out.println("[" + clientName + "] left the chat");
            sendMessageToAll(this, "[" + clientName + "] left the chat");
        }
        
        @Override
        public void run() {
            try {
                // Create input and output streams for communication
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                // Read name from client
                output.println("--------------[ Client ]--------------");
                output.println("Enter your name:");
                clientName = input.readLine();

                // Validate name until the client enters a valid name
                while (clientName == null || clientName.trim().isEmpty() || isNameTaken(clientName.trim())) {
                    output.println("Name is taken or invalid. Enter another name:");
                    clientName = input.readLine();
                }

                clientName = clientName.trim(); // Remove extra spaces (e.g. '  Alice  ' -> 'Alice')
                addClient(this); // Add client to the clients list

                showChatWindow(); // Display chat window

                // Show join notification message to the connected client, server, and other clients
                showJoinNotification(clientName);

                // Read client messages in chat and send it to other clients until client type 'exit'
                String clientMessage;
                while ((clientMessage = input.readLine()) != null) {
                    if (clientMessage.equalsIgnoreCase("exit"))
                        break;
                    sendMessageToAll(this, clientName + ": " + clientMessage);
                }

                // ====== After client typing 'exit' ======
                // Show leave notification message to the disconnected client, server, and other clients 
                // then Close connection (socket) and remove the client from the clients list
                showLeaveNotification(clientName);
                socket.close();
                removeClient(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
