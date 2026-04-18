//==============
//  Client Code
//==============
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Define server address 
        int port = 1234; // Define the port to connect to server

        // Create a socket to connect to the server
        try (Socket socket = new Socket(serverAddress, port)) {
            
            // Create input and output streams for communication
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Read user input from the console
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Thread-safe boolean flag to track whether the client has joined the chat
            AtomicBoolean isJoinedChat = new AtomicBoolean(false);  

            // Create and start a thread that handle messages from server
            Thread thread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = input.readLine()) != null) {

                        // Once client join chat, update flag and display Chat Window
                        if (serverMessage.contains("You joined the chat")) {
                            isJoinedChat.set(true);
                            System.out.println(serverMessage); 
                            System.out.print("\rYou: ");

                        // Inside chat window after joining chat
                        // Display (Message Ignored) for client message when server message received 
                        // to avoid conflicts
                        } else if (isJoinedChat.get()) {
                            System.out.print(" (Message Ignored)\n");
                            System.out.println(serverMessage);
                            System.out.print("\rYou: ");

                        // Before joining chat, simple server message
                        } else 
                            System.out.println(serverMessage); 

                    }
                } catch (Exception e) {
                    System.out.println("Disconnected from server: " + e.getMessage());
                }
            });
            thread.start();


            // Main thread to send messages to server
            String message;
            do {
                if (isJoinedChat.get()) 
                    System.out.print("\rYou: "); 
                message = userInput.readLine();
                output.println(message);
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            } while (message != null);

        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
    }
}