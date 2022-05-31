package server;

import client.requests.Request;
import client.requests.ReqType;
import client.*;
import server.responses.ResType;
import server.responses.Response;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ClientHandler implements Runnable{
    private static HashMap<User, String> users;
    private final Socket socket;
    private ObjectInputStream request;
    private ObjectOutputStream response;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        users = new HashMap<>();
        try {
            request = new ObjectInputStream(socket.getInputStream());
            response = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            System.out.println("Damnnn!");
        }
    }

    @Override
    public void run() {
        Request requested;
        try {
            while (socket.isConnected()) {
                requested = (Request) request.readObject();
                giveResponse(requested);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Damn!");
            close();
        }
    }
    private void giveResponse(Request requested) {
        if(requested.getType() == ReqType.LOGIN)
            login(requested);
        else if(requested.getType() == ReqType.SIGN_UP) {
            signUP();
        }
    }
    private void login(Request info) {
        String username = info.getDescription().substring(0, info.getDescription().indexOf(" "));
        String password = info.getDescription().substring(info.getDescription().indexOf(" ") + 1);
        User loginClient = searchClient(username);
        if(loginClient == null || !users.get(loginClient).equals(password)) {
            try {
                response.writeObject(new Response(ResType.LOGIN, "failed", null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("Welcome Back!");
            try {
                response.writeObject(new Response(ResType.LOGIN, "passed", loginClient)); //**************************//
            } catch (IOException e) {
                System.out.println("Damn!(login)");
            }
        }
    }
    private void signUP(Request info) {
        String description = info.getDescription();
        String username = description.substring(0, description.indexOf(" "));
        String password = description.substring(description.indexOf(" ") + 1, description.lastIndexOf(" "));
        String mail = description.substring(description.lastIndexOf(" ") + 1);
    }
    private User searchClient(String username) {
        for(User user : users.keySet())
            if(user.getUsername().equals(username))
                return user;
        return null;
    }
    private void close() {
        try {
            if (request != null)
                response.close();
            if (response != null)
                response.close();
            if (socket != null)
                socket.close();
        }
        catch (IOException e) {
            System.out.println("Damn!");
        }
    }
}
