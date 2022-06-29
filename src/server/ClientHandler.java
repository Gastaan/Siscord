package server;


import server.data.UserData;
import shared.requests.*;
import shared.responses.ChatResponse;
import shared.responses.Notification;
import shared.responses.PrivateChatListResponse;
import shared.responses.login.LoginResponse;
import shared.responses.login.LoginStatus;
import shared.responses.signup.SignUpResponse;
import shared.responses.signup.SignUpStatus;
import shared.user.User;
import shared.user.data.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable{
    //fields
    private static ConcurrentHashMap<User, String> users;
    private static ConcurrentHashMap<User, UserData> userData;
    private static HashSet<Server> servers;
    private final Socket socket;
    private ObjectInputStream request;
    private ObjectOutputStream response;
    private User servingUser;
    private ConcurrentHashMap<User, ClientHandler> onlineUsers;
    //constructor

    public ClientHandler(Socket socket) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        this.socket = socket;
        users = new ConcurrentHashMap<>();
        userData = new ConcurrentHashMap<>();
        onlineUsers = new ConcurrentHashMap<>();
        try {
            request = new ObjectInputStream(socket.getInputStream());
            response = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            System.out.println("Damnnn!");
        }
    }
    //methods
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
            login((LoginRequest) requested);
        else if(requested.getType() == ReqType.SIGN_UP)
            signUP((SignUpRequest)requested);
        else if(requested.getType() == ReqType.PRIVATE_CHAT_LIST)
            privateChatList((PrivateChatListRequest) requested);
        else if(requested.getType() == ReqType.CHAT_REQUEST)
            chat((ChatRequest) requested);
        else if(requested.getType() == ReqType.PRIVATE_CHAT_REACT)
            react((ReactRequest) requested);
        else if(requested.getType() == ReqType.NEW_PRIVATE_CHAT)
            newPrivateChat((NewPrivateChatRequest) requested);
    }
    private void newPrivateChat(NewPrivateChatRequest request) {
        User user1 = searchClient(request.getUser1());
        User user2 = searchClient(request.getUser2());
    }
    private void chat(ChatRequest request) {
        User requestedUser = searchClient(request.getRequestedUser());
        try {
            response.writeObject(new ChatResponse(userData.get(requestedUser).getPrivateChat(request.getUsername())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void privateChatList(PrivateChatListRequest requested) {
        User requestedUser = searchClient(requested.getUsername());
        ArrayList<String> chatNames = userData.get(requestedUser).getPrivateChatList();
        try {
            response.writeObject(new PrivateChatListResponse(chatNames));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void react(ReactRequest request) {
        User person = searchClient(request.getRequestedUsername());
        ArrayList<Message> personMessages = userData.get(person).getPrivateChats().get(request.getRequestingUser());
        ArrayList<Message> userMessages = userData.get(request.getRequestingUser()).getPrivateChats().get(person);
        personMessages.get(personMessages.indexOf(request.getMessage())).addReaction(request.getReact(), request.getRequestingUser().getUsername());
        userMessages.get(userMessages.indexOf(request.getMessage())).addReaction(request.getReact(), request.getRequestingUser().getUsername());
    }
    private void login(LoginRequest info) {
        String username, password;
        username = info.getUsername();
        password = info.getPassword();
        User loginClient = searchClient(username);
        if(loginClient == null || !users.get(loginClient).equals(password)) {
            try {
                response.writeObject(new LoginResponse(LoginStatus.FAILURE, null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("Welcome Back!");
            try {
                response.writeObject(new LoginResponse(LoginStatus.SUCCESS, loginClient));
                servingUser = loginClient;
            } catch (IOException e) {
                System.out.println("Damn!(login)");
            }
        }
    }
    private void signUP(SignUpRequest info) {
        String username , password , mail, phoneNumber;
        username = info.getUsername();
        password = info.getPassword();
        mail = info.getMail();
        phoneNumber = info.getPhoneNumber();
        try {
            SignUpStatus status = checkRegex(username, password, mail, phoneNumber);
            User newUser = null;
            if(status == SignUpStatus.VALID) {
                newUser = new User(username, mail, phoneNumber);
                users.put(newUser, password);
                userData.put(newUser, new UserData());
                servingUser = newUser;
            }
                response.writeObject(new SignUpResponse(status, newUser ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private SignUpStatus checkRegex(String username, String password, String mail, String phoneNumber) {
        String usernameRegex = "[a-zA-Z0-9]{6,}";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$";
        String mailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        String phoneNumberRegex = " ^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$ ";
        if(searchClient(username) == null) {
            if(match(username, usernameRegex) && match(password, passwordRegex) && match(mail, mailRegex) && (match(phoneNumber, phoneNumberRegex) || phoneNumber.equals("")))
                return SignUpStatus.VALID;
            else
                return SignUpStatus.INVALID;
        }
        else
            return SignUpStatus.DUPLICATE;
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
    private void notify(String notification) {
        try {
            response.writeObject(new Notification(notification));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void close() { //TODO: close connection
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
