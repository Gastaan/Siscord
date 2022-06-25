package server;

import client.requests.Request;
import client.requests.ReqType;
import client.*;
import server.responses.ResType;
import server.responses.Response;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            signUP(requested);
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
        try {
            response.writeObject(new Response(ResType.SIGNUP,   checkRegex(username, password, mail), null ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String checkRegex(String username, String password, String mail) {
        String usernameRegex = "[a-zA-Z0-9]{6,}";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$";
        String mailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        if(searchClient(username) != null) {
            if(match(username, usernameRegex) && match(password, passwordRegex) && match(mail, mailRegex))
                return "valid";
            else
                return "not valid";
        }
        else
            return "username exists";
    }
    private Boolean match(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
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
