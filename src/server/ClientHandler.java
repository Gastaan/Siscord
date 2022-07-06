package server;


import server.data.Chat;
import server.data.UserData;
import server.data.socialserver.SocialServer;
import shared.requests.*;
import shared.responses.*;
import shared.responses.addfriend.AddFriendResponse;
import shared.responses.addfriend.AddFriendResponseStatus;
import shared.responses.list.ChatListResponse;
import shared.responses.login.LoginResponse;
import shared.responses.login.LoginStatus;
import shared.responses.newprivatechat.NewPrivateChatResponse;
import shared.responses.newprivatechat.NewPrivateChatStatus;
import shared.responses.signup.SignUpResponse;
import shared.responses.signup.SignUpStatus;
import shared.user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//TODO : Refactor
public class ClientHandler implements Runnable{
    //fields
    private static ConcurrentHashMap<User, String> users;
    private static ConcurrentHashMap<User, UserData> userData;
    private static ConcurrentHashMap<User, Vector<ClientHandler>> onlineUsers;

    private static Vector<SocialServer> servers;
    private final Socket socket;
    private ObjectInputStream request;
    private ObjectOutputStream response;
    private User servingUser;
    private final String usernameRegex = "[a-zA-Z0-9]{6,}";
    private String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$";
    private final String mailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private String phoneNumberRegex = " ^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$ ";

    public ClientHandler(Socket socket) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        this.socket = socket;
        if(users == null)
            users = new ConcurrentHashMap<>();
        if(userData == null)
            userData = new ConcurrentHashMap<>();
        if(servers == null)
            servers = new Vector<>();
        if(onlineUsers == null)
            onlineUsers = new ConcurrentHashMap<>();
        try {
            request = new ObjectInputStream(socket.getInputStream());
            response = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            System.err.println("Can not connect client to client handler!");
        }
    } //Done
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
            System.out.println("Client disconnected!");
            close();
        }
    } //Done
    private void giveResponse(Request requested) {
        if(requested.getType() == ReqType.LOGIN)
            login((LoginRequest) requested);
        else if(requested.getType() == ReqType.SIGN_UP)
            signUP((SignUpRequest)requested);
        else if(requested.getType() == ReqType.PRIVATE_CHAT_LIST)
            privateChatList(requested);
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
        else if(requested.getType() == ReqType.GET_FRIENDS_LIST)
            getFriendsList((GetFriendsListRequest) requested);
        else if(requested.getType() == ReqType.REMOVE_FRIEND)
            removeFriend((RemoveFriendRequest) requested);
        else if(requested.getType() == ReqType.GET_OUTGOING_FRIEND)
            getOutgoingFriend();
        else if(requested.getType() == ReqType.GET_BLOCKED_USERS)
            getBlockedUsers();
        else if(requested.getType() == ReqType.UNBLOCK_USER)
            unblockUser((UnblockRequest)requested);
        else if (requested.getType() == ReqType.BLOCK_USER)
            blockUser((BlockRequest)requested);
        else if(requested.getType() == ReqType.NEW_SERVER)
            newServer((NewServerRequest) requested);
        else if(requested.getType() == ReqType.SERVER_LIST)
            serverList(requested);
        else if(requested.getType() == ReqType.SERVER_CHANELS)
            serverChanels((GetChanelsRequest) requested);
        else if(requested.getType() == ReqType.IS_TYPING)
            isTyping((IsTypingRequest) requested);
        else if(requested.getType() == ReqType.CHANGE_PASSWORD)
            changePassword((ChangePasswordRequest) requested);
    }
    private void changePassword(ChangePasswordRequest requested) {
         if(match(requested.getNewPassword(), passwordRegex)) {
             users.put(servingUser, requested.getNewPassword());
             try {
                 response.writeObject(new ChangePasswordResponse(true));
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }
         else
                try {
                    response.writeObject(new ChangePasswordResponse(false));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
    }
    private void isTyping(IsTypingRequest requested) {
      for(ClientHandler ch  : onlineUsers.get(searchUser(requested.getUsername())))
          ch.sendNotification(servingUser.getUsername() + " is typing...");
    }
    private void serverChanels(GetChanelsRequest requested) {
        int serverID = requested.getServerID();
        SocialServer server = servers.get(serverID);
        ArrayList<String> chanels = server.getChanels();
        try {
            response.writeObject(new ChanelListResponse(serverID, chanels));
        }
        catch (IOException e) {
            System.err.println("Can not send response to client!");
        }
    }
    private void serverList(Request requested) {
        HashMap<Integer, String> serverList = new HashMap<>();
        synchronized (servers) {
            for(Integer server : userData.get(servingUser).getServers())
                for(SocialServer s : servers)
                    if(s.getServerID() == server)
                        serverList.put(s.getServerID(), s.getServerName());
        }
        try {
            response.writeObject(new ServerListResponse(serverList));
        }
        catch (IOException e) {
            System.err.println("Can not send server list to client!");
        }
    } //Done
    private void newServer(NewServerRequest requested) { //TODO : here
        SocialServer server = new SocialServer(requested.getServerName(), servingUser.getUsername());
        servers.add(server);
        userData.get(servingUser).addServer(server.getServerID());
        try {
            response.writeObject(new NewServerResponse(true, server.getServerID()));
        }
        catch (IOException e) {
            System.err.println("Can not send new server response!");
        }
    }
    private void blockUser(BlockRequest requested) {
        User blockingUser = searchUser(requested.getUsername());
        boolean success;
        if (blockingUser == null || blockingUser == servingUser)
            success = false;
        else {
            userData.get(servingUser).blockUser(blockingUser.getUsername());
            success = true;
        }
        try {
            response.writeObject(new BlockResponse(success));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void unblockUser(UnblockRequest requested) { //TODO : Failed to unblock user
        userData.get(servingUser).unblockUser(requested.getUsername());
        try {
            response.writeObject(new UnblockResponse(true));
        }
        catch (IOException e) {
            System.err.println("Can not send response to client!");
        }
    }
    private void getBlockedUsers() {
        try {
            response.writeObject(new GetBlockedUsersResponse(userData.get(servingUser).getBlockedUsers()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void getOutgoingFriend() {
        try {
            response.writeObject(new GetOutgoingFriendResponse(userData.get(servingUser).getOutgoingFriendRequests()));
        }
        catch (IOException e) {
            System.err.println("Can not send outgoing friend to client!");
        }
    } //Done
    private void removeFriend(RemoveFriendRequest request) {
        User requestedUser = searchUser(request.getRequestedUser());
        userData.get(servingUser).deleteFriend(requestedUser.getUsername());
        userData.get(requestedUser).deleteFriend(servingUser.getUsername());
    } //Done
    private void getFriendsList(GetFriendsListRequest requested) {
        User requestedUser = searchUser(requested.getUsername());
        ArrayList<String> friends = new ArrayList<>();
        for(String friend : userData.get(requestedUser).getFriends()) {
            friends.add(searchUser(friend).userStatus());
        }
        try {
            response.writeObject(new GetFriendsListResponse(friends));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addFriend(AddFriendRequest requested) {
        User requesting = searchUser(requested.getRequestingUser());
        User requestedFriend = searchUser(requested.getRequestedUser());
        AddFriendResponseStatus  status;
        if(requestedFriend == null || requesting == requestedFriend)
            status = AddFriendResponseStatus.USER_NOT_FOUND;
        else if(userData.get(requesting).isFriend(requestedFriend.getUsername()))
            status = AddFriendResponseStatus.ALREADY_FRIENDS;
        else {
            status = AddFriendResponseStatus.FRIEND_REQUEST_SENT;
            userData.get(requesting).addOutgoingFriendRequest(requestedFriend.getUsername());
            userData.get(requestedFriend).addIncomingFriendRequest(requesting.getUsername());
            if(onlineUsers.get(requestedFriend) != null)
                for(ClientHandler ch : onlineUsers.get(requestedFriend))
                 ch.sendNotification("Friend request from " + requesting.getUsername());
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
        userData.get(servingUser).deleteIncomingFriendRequest(requestedUser.getUsername());
        userData.get(requestedUser).deleteOutgoingFriendRequest(servingUser.getUsername());
        String notificationForRequestedUser = requestingUser.getUsername();
        notificationForRequestedUser += requested.isAccept() ? " accepted your friend request" : " rejected your friend request!";
        if(requested.isAccept()) {
            userData.get(requestedUser).addFriend(requestingUser.getUsername());
            userData.get(requestingUser).addFriend(requestedUser.getUsername());
        }
        if(onlineUsers.get(requestedUser) != null)
            for(ClientHandler ch : onlineUsers.get(requestedUser))
                ch.sendNotification(notificationForRequestedUser);
    }
    private void getFriendRequests(GetFriendRequestsRequest requested) {
        User requestedUser = searchUser(requested.getRequestedUser());
        try {
            response.writeObject(new IncomingFriendRequestsResponse(userData.get(requestedUser).getIncomingFriendRequests()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void newPrivateChat(NewPrivateChatRequest request) {
        User wantedUser = searchUser(request.getUsername());
        NewPrivateChatStatus status;
        if (wantedUser == null)
            status = NewPrivateChatStatus.USER_NOT_FOUND;
        else if(userData.get(servingUser).getPrivateChat(wantedUser.getUsername()) != null)
            status = NewPrivateChatStatus.ALREADY_CHAT_EXISTS;
        else {
            status = NewPrivateChatStatus.CHAT_CREATED;
            userData.get(servingUser).newPrivateChat(wantedUser.getUsername());
            userData.get(wantedUser).newPrivateChat(servingUser.getUsername());
            if(onlineUsers.get(wantedUser) != null)
                for(ClientHandler ch : onlineUsers.get(wantedUser))
                    ch.sendNotification("New private chat with " + servingUser.getUsername() + " just created!");
        }
        try {
            response.writeObject(new NewPrivateChatResponse(status));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void privateChatList(Request requested) {
        ArrayList<String> chatNames = userData.get(servingUser).getPrivateChatList();
        try {
            response.writeObject(new ChatListResponse(chatNames));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void chat(ChatRequest request) {
        Chat privateChat;
        String direction = request.getUsername();
        if(request.getUsername().contains("Server")) {
         int serverID = Integer.parseInt(request.getUsername().substring(direction.lastIndexOf(" ") + 1));
         String chanelName = direction.substring(direction.indexOf(" ") + 1, direction.lastIndexOf(" "));
         privateChat = servers.get(servers.indexOf(searchServerByID(serverID))).getTextChanel(chanelName);
        }
        else
            privateChat = userData.get(servingUser).getPrivateChat(request.getUsername());
        try {
            reset();
            response.writeObject(new ChatResponse(privateChat.getMessages(), request.getUsername()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private SocialServer searchServerByID(int id) {
        synchronized (servers) {
            for (SocialServer server : servers)
                if (server.getServerID() == id)
                    return server;
        }
        return null;
    }
    private void reset() {
        synchronized (response) {
            try {
                response.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void sendMessage(NewPrivateChatMessageRequest request) {
        User receiver = searchUser(request.getReceiver());
        userData.get(servingUser).getPrivateChat(receiver.getUsername()).addMessage(request.getMessage());
        userData.get(receiver).getPrivateChat(servingUser.getUsername()).addMessage(request.getMessage());
        try {
            response.writeObject(new NewMessageResponse(true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void react(ReactRequest request) {
        User user = searchUser(request.getChatName());
        userData.get(servingUser).getPrivateChat(user.getUsername()).addReaction(servingUser.getUsername(), request.getTime(), request.getReact());
        userData.get(user).getPrivateChat(servingUser.getUsername()).addReaction(servingUser.getUsername(), request.getTime(), request.getReact());
        try {
            response.writeObject(new ReactResponse(true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void logOut() {} //TODO : log out
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
                if(onlineUsers.keySet().contains(loginClient))
                    onlineUsers.get(loginClient).add(this);
                else {
                    Vector<ClientHandler> clients = new Vector<>();
                    clients.add(this);
                    onlineUsers.put(loginClient, clients);
                }
            } catch (IOException e) {
                System.out.println("Damn!(login)");
            }
        }
    } //Done
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
                if(onlineUsers.containsKey(newUser))
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
    } //Done
    private SignUpStatus checkRegex(String username, String password, String mail, String phoneNumber) {
        if(searchUser(username) == null) {
            if(match(username, usernameRegex) && match(password, passwordRegex) && match(mail, mailRegex) && (match(phoneNumber, phoneNumberRegex) || phoneNumber.equals("")))
                return SignUpStatus.VALID;
            else
                return SignUpStatus.INVALID;
        }
        else
            return SignUpStatus.DUPLICATE;
    }//Done
    private Boolean match(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    } //Done
    private static User searchUser(String username) {
        for(User user : users.keySet())
            if(user.getUsername().equals(username))
                return user;
        return null;
    } //Done
    private  void sendNotification(String notification) {
        synchronized (response) {
            try {
                response.writeObject(new Notification(notification));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    } //Done
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
            System.out.println("Couldn't close resources");
        }
    }
}
