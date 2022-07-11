package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public void startServer() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("A user has been connected!");
                new Thread(new ClientHandler(socket)).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) {
        try {
            Server server = new Server(new ServerSocket(3132));
            server.startServer();
        } catch (IOException e) {
            System.err.println("Server is under under maintenance!");
        }
    }
}
