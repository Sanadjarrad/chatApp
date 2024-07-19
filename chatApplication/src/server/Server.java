package server;
import javax.sound.sampled.Port;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class Server {

    private static List<ClientHandler> connectedClients = new ArrayList<>();

    public Server() throws IOException {
        connectedClients = new ArrayList<ClientHandler>();
    }


    public static void main(String[] args) throws IOException {
        int portNum = 8080;
        ServerSocket socket = new ServerSocket(portNum);
        System.out.print("Server started on port: " + socket.getLocalPort());

        Socket client = socket.accept();
        System.out.println("Client connected to server");

        while (true) {
            Socket clientSocket = socket.accept();
            System.out.println("Client connected to server on port: " + portNum);
            ClientHandler clientHandler = new ClientHandler(clientSocket, connectedClients);
            connectedClients.add(clientHandler);
            new Thread(clientHandler).start();
        }

    }

    /*
    public static int getPort() {
        return Server.portNum;
    }

     */
}

class ClientHandler implements Runnable{

    private Socket clientSocket;
    // Clients list
    private List<ClientHandler> clients;
    // Broadcast messages
    private PrintWriter writer;
    // Read client messages
    private BufferedReader reader;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws UnknownHostException, IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public ClientHandler() throws UnknownHostException, IOException {
        this.clientSocket = new Socket("Localhost", 8080);
        this.clients = new ArrayList<ClientHandler>();
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    @Override
    public void run() {
        try {
            String input;
            while ((input = reader.readLine()) != null) {
                for (ClientHandler client : clients) {
                    // Display message for all clients
                    client.writer.println(input);
                }
            }
        } catch (IOException ioException) {
            String message = ioException.getMessage();
            System.out.println("Runtime error occured " + message);
            ioException.printStackTrace();
        }
        try {
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}

