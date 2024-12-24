import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String username;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Chat Application");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JTextField inputField = new JTextField();
        frame.add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                sendMessage(username + ": " + message);
                inputField.setText("");
            }
        });

        frame.setVisible(true);

        // Connect to the server
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            username = JOptionPane.showInputDialog("Enter your username:");
            sendMessage(username + " has joined the chat!");

            // Receive messages
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        chatArea.append(serverMessage + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Unable to connect to the server.");
            System.exit(1);
        }
    }

    private static void sendMessage(String message) {
        out.println(message);
    }
}
