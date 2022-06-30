package server;


import server.data.PrivateChat;
import server.data.UserData;
import shared.requests.*;
import shared.responses.*;
import shared.responses.addfriend.AddFriendResponse;
import shared.responses.addfriend.AddFriendResponseStatus;
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
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable{
    //fields
    public static ConcurrentHashMap<User, String> users;
    private static ConcurrentHashMap<User, UserData> userData;
    private static HashSet<Server> servers;
    private final Socket socket;
    private ObjectInputStream request;
    private ObjectOutputStream response;
    private User servingUser;
    private ConcurrentHashMap<User, Vector<ClientHandler>> onlineUsers;
    //constructor

    public ClientHandler(Socket socket) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        this.socket = socket;
        if(users == null)
            users = new ConcurrentHashMap<>();
        if(userData == null)
            userData = new ConcurrentHashMap<>();
        if(servers == null)
            servers = new HashSet<>();
        if(onlineUsers == null)
            onlineUsers = new ConcurrentHashMap<>();
        try {
            request = new ObjectInputStream(socket.getInputStream());
            response = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            System.err.println("Can not connect client to client handler!");
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
        else if(requested.getType() == ReqType.NEW_PRIVATE_CHAT_MESSAGE)
            sendMessage((NewPrivateChatMessageRequest) requested);
        else if(requested.getType() == ReqType.GET_FRIEND_REQUESTS)
            getFriendRequests((GetFriendRequestsRequest) requested);
        else if(requested.getType() == ReqType.FRIEND_REQUEST_ANSWER)
            friendRequestAnswer((FriendRequestAnswerRequest) requested);
        else if(requested.getType() == ReqType.ADD_FRIEND)
            addFriend((AddFriendRequest) requested);
    }
    private void addFriend(AddFriendRequest requested) {
        User requesting = searchUser(requested.getRequestingUser());
        User requestedFriend = searchUser(requested.getRequestedUser());
        AddFriendResponseStatus  status;
        if(requestedFriend == null)
            status = AddFriendResponseStatus.USER_NOT_FOUND;
        else if(userData.get(requesting).isFriend(requestedFriend.getUsername()))
            status = AddFriendResponseStatus.ALREADY_FRIENDS;
        else {
            status = AddFriendResponseStatus.FRIEND_REQUEST_SENT;
            userData.get(requesting).addOutgoingFriendRequest(requestedFriend.getUsername());
            userData.get(requestedFriend).addIncomingFriendRequest(requesting.getUsername());
            if(onlineUsers.get(requestedFriend) != null)
                for(ClientHandler ch : onlineUsers.get(requestedFriend))
                 ch.notify("Friend request from " + requesting.getUsername());
        }
        try {
            response.writeObject(new AddFriendResponse(status, requesting.getUsername()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void  friendRequestAnswer(FriendRequestAnswerRequest requested) {
        User requestingUser = searchUser(requested.getRequestingUser());
        User requestedUser = searchUser(requested.getRequestedUser());
        userData.get(requestingUser).deleteIncomingFriendRequest(requestedUser.getUsername());
        userData.get(requestingUser).deleteOutgoingFriendRequest(requestedUser.getUsername());
        String notificationForRequestedUser = requestingUser.getUsername();
        notificationForRequestedUser += requested.isAccept() ? " accepted your friend request" : " rejected your friend request!";
        if(requested.isAccept()) {
            userData.get(requestedUser).addFriend(requestingUser.getUsername());
            userData.get(requestingUser).addFriend(requestedUser.getUsername());
        }
        for(ClientHandler clientHandler : onlineUsers.get(requestedUser)) {
            clientHandler.notify(notificationForRequestedUser);
        }
    }
    private void getFriendRequests(GetFriendRequestsRequest requested) {
        User requestedUser = searchUser(requested.getRequestedUser());
        try {
            response.writeObject(new IncomingFriendRequestsResponse(userData.get(requestedUser).getIncomingFriendRequests()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void newPrivateChat(NewPrivateChatRequest request) { //TODO : new private chat
        User user1 = searchUser(request.getUser1());
        User user2 = searchUser(request.getUser2());
    }
    private void privateChatList(PrivateChatListRequest requested) {
        User requestedUser = searchUser(requested.getUsername());
        ArrayList<String> chatNames = userData.get(requestedUser).getPrivateChatList();
        try {
            response.writeObject(new PrivateChatListResponse(chatNames));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void chat(ChatRequest request) {
        User requestedUser = searchUser(request.getRequestedUser());
        try {
            PrivateChat privateChat = userData.get(requestedUser).getPrivateChat(request.getUsername());
            response.writeObject(new ChatResponse(privateChat.getMessages()));
            privateChat.addInChat(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendMessage(NewPrivateChatMessageRequest request) {
        User sender = searchUser(request.getSender());
        User receiver = searchUser(request.getReceiver());
        userData.get(sender).getPrivateChat(receiver.getUsername()).addMessage(request.getMessage());
        userData.get(receiver).getPrivateChat(sender.getUsername()).addMessage(request.getMessage());
    }
    private void react(ReactRequest request) {
        /*
        User person = searchClient(request.getRequestedUsername());
        ArrayList<Message> personMessages = userData.get(person).getPrivateChats().get(request.getRequestingUser());
        ArrayList<Message> userMessages = userData.get(request.getRequestingUser()).getPrivateChats().get(person);
        personMessages.get(personMessages.indexOf(request.getMessage())).addReaction(request.getReact(), request.getRequestingUser().getUsername());
        userMessages.get(userMessages.indexOf(request.getMessage())).addReaction(request.getReact(), request.getRequestingUser().getUsername());

         */
    } //TODO : add reaction to message
    private void login(LoginRequest info) {
        String username, password;
        username = info.getUsername();
        password = info.getPassword();
        User loginClient = searchUser(username);
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
                Vector<ClientHandler> clients = onlineUsers.get(loginClient);
                if(clients != null)
                    clients.add(this);
                else {
                    clients = new Vector<>();
                    clients.add(this);
                    onlineUsers.put(loginClient, clients);
                }
            } catch (IOException e) {
                System.out.println("Damn!(login)");
            }
        }
    }
    private void logOut() {} //TODO : log out
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
                if(onlineUsers.get(newUser) != null)
                    onlineUsers.get(newUser).add(this);
                else {
                    Vector<ClientHandler> clients = new Vector<>();
                    clients.add(this);
                    onlineUsers.put(newUser, clients);
                }
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
        if(searchUser(username) == null) {
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
    private static User searchUser(String username) {
        for(User user : users.keySet())
            if(user.getUsername().equals(username))
                return user;
        return null;
    }
    private synchronized void notify(String notification) {
        try {
            response.writeObject(new Notification(notification));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void newMessage(Message message) {
        try {
            response.writeObject(new NewMessageResponse(message));
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
