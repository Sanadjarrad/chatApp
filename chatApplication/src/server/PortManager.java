package server;

import java.io.*;
import java.net.DatagramSocket;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.SocketException;

public class PortManager {

    private static final String PORT_FILE = "port.txt";

    public PortManager() throws IOException {
        this.findFreePortInRange(0, 65536);
    }

    public static int findFreePortInRange(int startPort, int endPort) throws IOException {
        for (int port = startPort; port <= endPort; port++) {
            try (ServerSocket socket = new ServerSocket(port)) {
                return port;
            }
        }
        throw new IOException("No free port found in the specified range");
    }


    public static boolean portIsAvailable(int port) throws IOException {
        final int MIN_PORT_NUM = 0;
        final int MAX_PORT_NUM = 65535;

        if (port < MIN_PORT_NUM || port > MAX_PORT_NUM) {
            throw new IllegalArgumentException("Invalid port number: " + port);
        }
        ServerSocket serverSocket = null;
        DatagramSocket datagramSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setReuseAddress(true);
            return true;
        } catch (SocketException se) {
            se.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            } if (serverSocket != null) {
                datagramSocket.close();
            }
        }
        return false;
    }

    public static void writePort(int port) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PORT_FILE))) {
            writer.println(port);
        }
    }

    public static int readPort() throws IOException, PortUnreachableException {
        try (BufferedReader reader = new BufferedReader(new FileReader(PORT_FILE))) {
            return Integer.parseInt(reader.readLine().trim());
        }
    }
}
