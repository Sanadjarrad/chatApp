package client;

import server.Server;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatGUI extends JFrame {

    private JTextArea messagesFrame;
    private JTextField textField;
    private JButton exitButton;
    private JButton sendButton;
    private Client client;
    private final String HOST = "localhost";

    public ChatGUI() {
        super("Chat application");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color background = new Color(240, 240, 240);
        Color button = new Color(50, 50, 50);
        Color text = new Color(50, 50, 50);
        Font font = new Font("Times new roman", Font.PLAIN, 16);
        Font buttonFont = new Font("Times New Roman", Font.BOLD, 18);

        messagesFrame = new JTextArea();
        messagesFrame.setEditable(false);
        messagesFrame.setBorder(BorderFactory.createTitledBorder("Chat"));
        messagesFrame.setFont(font);
        messagesFrame.setBackground(background);
        messagesFrame.setForeground(text);
        JScrollPane scrollPane = new JScrollPane(messagesFrame);
        add(scrollPane, BorderLayout.CENTER);

        String userName = JOptionPane.showInputDialog(this, "Enter Username: ", JOptionPane.PLAIN_MESSAGE);
        this.setTitle("Basic Java Chat Application - User: " + userName);
        textField = new JTextField();
        //textField.setBorder(BorderFactory.createTitledBorder("Type messages here"));
        textField.setFont(font);
        textField.setBackground(background);
        textField.setForeground(text);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + userName + ": "
                        + textField.getText();
                client.sendMessage(message);
                textField.setText("");
            }
        });
        add(textField, BorderLayout.SOUTH);

        sendButton = new JButton("Send");
        sendButton.setFont(buttonFont);
        sendButton.setBackground(background);
        sendButton.setForeground(button);
        /*
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

         */

        exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);
        exitButton.setBackground(button);
        exitButton.setForeground(button);
        exitButton.addActionListener(e -> {
            String exitMessage = userName + " has exited the chat room";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }

            System.exit(0);
        });
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(textField, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1,2));
        buttonPanel.add(sendButton);
        buttonPanel.add(exitButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        try {
            this.client = new Client(HOST, 8080, this::messageReceived);
            client.sendMessage(userName + " Has Joined the Chat");
            client.start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occured while connecting to the server", "Connection error", JOptionPane.ERROR_MESSAGE);
            System.exit(5);
        }

    }

    public void sendMessage() {
        String user  = this.getTitle().split(" ")[1];
        String message = textField.getText();
        if (message.length() > 0) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String formattedMessage = "[" + timestamp + "] " + user + ": " + message;
            messagesFrame.append(formattedMessage + "\n");
            client.sendMessage(formattedMessage);
            textField.setText("");
        }
    }

    public void messageReceived(String message) {
        SwingUtilities.invokeLater(() -> messagesFrame.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater( () -> new ChatGUI().setVisible(true));
    }

}
