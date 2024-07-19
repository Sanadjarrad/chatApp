package client;


import server.PortManager;
import server.Server;

import javax.sound.sampled.Port;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.function.Consumer;

public class Client {

    private final static String HOST = "Localhost";
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> onMessageRecieved;

    public Client(String address, int port, Consumer<String> onMessageRecieved) throws IOException, SocketException, UnknownHostException {
        this.socket = new Socket(address, port);
        //System.out.println("Connected to chat server on port: " + port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.onMessageRecieved = onMessageRecieved;

    }

    public Client (String address, int port) throws IOException, SocketException {
        socket = new Socket(address, port);
        in = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String l = "";
        while (!l.equals("exit")) {
            l = in.readLine();
            out.println(l);
            System.out.println(in.readLine());
        }
        socket.close();
        in.close();
        out.close();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void start() {
        new Thread(() -> {
            try {
                String l;
                while ((l = in.readLine()) != null) {
                    onMessageRecieved.accept(l);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).start();
    }

}
