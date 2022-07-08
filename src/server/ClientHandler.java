package server;


import server.data.Chat;
import server.data.UserData;
import server.data.socialserver.Roles;
import server.data.socialserver.SocialServer;
import server.data.socialserver.chanel.Chanel;
import server.data.socialserver.chanel.TextChanel;
import shared.requests.*;
import shared.responses.*;
import shared.responses.addfriend.AddFriendResponse;
import shared.responses.addfriend.AddFriendResponseStatus;
import shared.responses.list.ListResponse;
import shared.responses.login.LoginResponse;
import shared.responses.login.LoginStatus;
import shared.responses.newprivatechat.NewPrivateChatResponse;
import shared.responses.newprivatechat.NewPrivateChatStatus;
import shared.responses.signup.SignUpResponse;
import shared.responses.signup.SignUpStatus;
import shared.user.User;
import shared.user.UserStatus;
import shared.user.data.message.Message;
import shared.user.data.message.TextMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author saman hazemi
 * This class is a client handler.
 * It is used to handle the client requests.
 */
public class ClientHandler implements Runnable{
    //fields
    private static ConcurrentHashMap<User, UserData> userData;
    private static ConcurrentHashMap<String, Vector<ClientHandler>> onlineUsers;
    private static Vector<SocialServer> servers;
    private final Socket socket;
    private ObjectInputStream request;
    private ObjectOutputStream response;
    private User servingUser;
    private final String usernameRegex = "[a-zA-Z0-9]{6,}";
    private final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$";
    private final String mailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private final String phoneNumberRegex = " ^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$ ";

    /**
     * constructor of the client handler class.
     * @param socket the socket of the client.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
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
            close();
        }
    } //Done
    //methods

    /**
     * This method gets the request from the client.
     */
    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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

    /**
     * This method gives the response to the client.
     * @param requested the request from the client.
     */
    private void giveResponse(Request requested) throws IOException {
        if(requested.getType() == ReqType.LOGIN)
            login((LoginRequest) requested);
        else if(requested.getType() == ReqType.SIGN_UP)
            signUP((SignUpRequest)requested);
        else if(requested.getType() == ReqType.PRIVATE_CHAT_LIST)
            privateChatList();
        else if(requested.getType() == ReqType.CHAT_REQUEST)
            chat((PlaceholderRequest) requested);
        else if(requested.getType() == ReqType.CHAT_REACT)
            react((ReactRequest) requested);
        else if(requested.getType() == ReqType.NEW_PRIVATE_CHAT)
            newPrivateChat((StringRequest) requested);
        else if(requested.getType() == ReqType.NEW_MESSAGE)
            sendMessage((NewMessageRequest) requested);
        else if(requested.getType() == ReqType.GET_INCOMING_FRIEND_REQUESTS)
            getFriendRequests();
        else if(requested.getType() == ReqType.FRIEND_REQUEST_ANSWER)
            friendRequestAnswer((FriendRequestAnswerRequest) requested);
        else if(requested.getType() == ReqType.ADD_FRIEND)
            addFriend((StringRequest) requested);
        else if(requested.getType() == ReqType.GET_FRIENDS_LIST)
            getFriendsList();
        else if(requested.getType() == ReqType.REMOVE_FRIEND)
            removeFriend((StringRequest) requested);
        else if(requested.getType() == ReqType.GET_OUTGOING_FRIEND)
            getOutgoingFriend();
        else if(requested.getType() == ReqType.GET_BLOCKED_USERS)
            getBlockedUsers();
        else if(requested.getType() == ReqType.UNBLOCK_USER)
            unblockUser((StringRequest)requested);
        else if (requested.getType() == ReqType.BLOCK_USER)
            blockUser((StringRequest)requested);
        else if(requested.getType() == ReqType.NEW_SERVER)
            newServer((StringRequest) requested);
        else if(requested.getType() == ReqType.SERVER_LIST)
            serverList();
        else if(requested.getType() == ReqType.SERVER_CHANELS)
            serverChanels((GetChanelsRequest) requested);
        else if(requested.getType() == ReqType.IS_TYPING)
            isTyping((PlaceholderRequest) requested);
        else if(requested.getType() == ReqType.CHANGE_PASSWORD)
            changePassword((StringRequest) requested);
        else if(requested.getType() == ReqType.PIN_MESSAGE)
            pin((PinRequest) requested);
        else if(requested.getType() == ReqType.CANCEL_FRIEND_REQUEST)
            cancelFriendRequest((StringRequest) requested);
    }
    private void cancelFriendRequest(StringRequest requested) {
        userData.get(servingUser).deleteOutgoingFriendRequest(requested.getValue());
        userData.get(searchUser(requested.getValue())).deleteIncomingFriendRequest(servingUser.getUsername());
    }
    private void pin(PinRequest request) throws IOException {
        String[] placeholders = request.getPlaceHolder();
        if(placeholders.length == 1) {
            User user =  searchUser(placeholders[0]);
            userData.get(servingUser).getPrivateChat(user.getUsername()).pinMessage(request.getTime());
            userData.get(user).getPrivateChat(servingUser.getUsername()).pinMessage(request.getTime());
        }
        else {
            int serverID = Integer.parseInt(placeholders[0]);
            SocialServer socialServer = servers.get(searchServerByID(serverID));
            TextChanel chanel = socialServer.getTextChanel(placeholders[1]);
            chanel.getChat().pinMessage(request.getTime());
        }
        response.writeObject(new PinResponse(true));
    }

    /**
     * This method changes the password of the user.
     * @param requested the change password request from the client.
     */
    private void changePassword(StringRequest requested) {
         if(match(requested.getValue(), passwordRegex)) {
             userData.get(servingUser).changePassword(requested.getValue());
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
    private void sendNotification(String notification, String username) {
        if(!username.equals(servingUser.getUsername()))
            for(ClientHandler ch : onlineUsers.get(username))
                ch.getNotification(notification);
    }
    private HashSet<String> qualifyServerMembersToGetNotification(SocialServer notifyingServer, Chanel textChanel) {
        if(textChanel.getIsLimited()) {
            HashSet<String> accessUsers = textChanel.getAccessList();
            accessUsers.addAll(notifyingServer.getRoles(Roles.LIMIT_MEMBERS));
            return accessUsers;
        }
        else
            return notifyingServer.getMembers();
    }
    private void isTyping(PlaceholderRequest requested) {
        String[] placeholder = requested.getPlaceholder();
      if(placeholder.length == 1)
              sendNotification(servingUser.getUsername() + "is typing...", placeholder[0]);
      else {
          SocialServer notifyingServer = servers.get(searchServerByID(Integer.parseInt(placeholder[0])));
          Chanel textChanel = notifyingServer.getTextChanel(placeholder[1]);
          for(String username : qualifyServerMembersToGetNotification(notifyingServer, textChanel))
              sendNotification(servingUser.getUsername() + "is typing...", username);
      }
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
    private void serverList() {
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
    private void newServer(StringRequest requested) { //TODO : here
        SocialServer server = new SocialServer(requested.getValue(), servingUser.getUsername());
        servers.add(server);
        userData.get(servingUser).addServer(server.getServerID());
        try {
            response.writeObject(new NewServerResponse(true, server.getServerID()));
        }
        catch (IOException e) {
            System.err.println("Can not send new server response!");
        }
    }
    private void blockUser(StringRequest requested) throws IOException {
        User blockingUser = searchUser(requested.getValue());
        boolean success;
        if (blockingUser == null || blockingUser == servingUser)
            success = false;
        else {
            userData.get(servingUser).blockUser(blockingUser.getUsername());
            success = true;
        }
        response.writeObject(new BlockResponse(success));
    }
    private void unblockUser(StringRequest requested) { //TODO : Failed to unblock user
        userData.get(servingUser).unblockUser(requested.getValue());
        try {
            response.writeObject(new UnblockResponse(true));
        }
        catch (IOException e) {
            System.err.println("Can not send response to client!");
        }
    }
    private void getBlockedUsers() throws IOException {
        response.writeObject(new ListResponse(userData.get(servingUser).getBlockedUsers()));
    }

    /**
     * This method is used to send outgoing friend requests to the client.
     * @throws IOException if the response can not be sent to the client.
     */
    private void getOutgoingFriend() throws IOException {
        response.writeObject(new ListResponse(userData.get(servingUser).getOutgoingFriendRequests()));
    }
    private void removeFriend(StringRequest request) {
        User requestedUser = searchUser(request.getValue());
        userData.get(servingUser).deleteFriend(requestedUser.getUsername());
        userData.get(requestedUser).deleteFriend(servingUser.getUsername());
    } //Done
    private void getFriendsList() throws IOException {
        User requestedUser = searchUser(servingUser.getUsername());
        ArrayList<String> friends = new ArrayList<>();
        for(String friend : userData.get(requestedUser).getFriends())
            friends.add(searchUser(friend).userStatus());
        response.writeObject(new ListResponse(friends));
    }

    /**
     * This method gives response for add friend request.
     * @param requested the add friend request from the client.
     * @throws IOException if the response can not be sent to the client.
     */
    private void addFriend(StringRequest requested) throws IOException {
        User requesting = servingUser;
        User requestedFriend = searchUser(requested.getValue());
        AddFriendResponseStatus  status;
        if(requestedFriend == null || requesting == requestedFriend)
            status = AddFriendResponseStatus.USER_NOT_FOUND;
        else if(userData.get(requesting).isFriend(requestedFriend.getUsername()))
            status = AddFriendResponseStatus.ALREADY_FRIENDS;
        else {
            status = AddFriendResponseStatus.FRIEND_REQUEST_SENT;
            userData.get(requesting).addOutgoingFriendRequest(requestedFriend.getUsername());
            userData.get(requestedFriend).addIncomingFriendRequest(requesting.getUsername());
            if(onlineUsers.keySet().contains(requestedFriend.getUsername()))
                for(ClientHandler ch : onlineUsers.get(requestedFriend.getUsername()))
                    ch.getNotification("Friend request from " + requesting.getUsername());
        }
        response.writeObject(new AddFriendResponse(status, requesting.getUsername()));
    }

    /**
     * This method gives response for friend request answer.
     * @param requested the friend request answer from the client.
     */
    private void  friendRequestAnswer(FriendRequestAnswerRequest requested) {
        User requestingUser = servingUser;
        User requestedUser = searchUser(requested.getRequestedUser());
        userData.get(servingUser).deleteIncomingFriendRequest(requestedUser.getUsername());
        userData.get(requestedUser).deleteOutgoingFriendRequest(servingUser.getUsername());
        String notificationForRequestedUser = requestingUser.getUsername();
        notificationForRequestedUser += requested.isAccept() ? " accepted your friend request" : " rejected your friend request!";
        if(requested.isAccept()) {
            userData.get(requestedUser).addFriend(requestingUser.getUsername());
            userData.get(requestingUser).addFriend(requestedUser.getUsername());
        }
        if(onlineUsers.keySet().contains(requestedUser.getUsername()))
            for(ClientHandler ch : onlineUsers.get(requestedUser.getUsername()))
                ch.getNotification(notificationForRequestedUser);
    }

    private void getFriendRequests() throws IOException {
        response.writeObject(new ListResponse(userData.get(servingUser).getIncomingFriendRequests()));
    }
    private void newPrivateChat(StringRequest request) {
        User wantedUser = searchUser(request.getValue());
        NewPrivateChatStatus status;
        if (wantedUser == null)
            status = NewPrivateChatStatus.USER_NOT_FOUND;
        else if(userData.get(servingUser).getPrivateChat(wantedUser.getUsername()) != null)
            status = NewPrivateChatStatus.ALREADY_CHAT_EXISTS;
        else {
            status = NewPrivateChatStatus.CHAT_CREATED;
            userData.get(servingUser).newPrivateChat(wantedUser.getUsername());
            userData.get(wantedUser).newPrivateChat(servingUser.getUsername());
            if(onlineUsers.get(wantedUser.getUsername()) != null)
                for(ClientHandler ch : onlineUsers.get(wantedUser.getUsername()))
                    ch.getNotification("New private chat with " + servingUser.getUsername() + " just created!");
        }
        try {
            response.writeObject(new NewPrivateChatResponse(status));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void privateChatList() throws IOException {
        ArrayList<String> chatNames = userData.get(servingUser).getPrivateChatList();
        response.writeObject(new ListResponse(chatNames));
    }
    private void chat(PlaceholderRequest request) {
        Chat chat = getChat(request.getPlaceholder());
        try {
            response.writeObject(new ChatResponse(chat.getMessages(), chat.getPinnedMessages(), request.getPlaceholder()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Chat getChat(String[] placeholder) {
        Chat chat;
        if(placeholder.length == 1)
            chat = userData.get(servingUser).getPrivateChat(placeholder[0]);
        else
            chat = servers.get(searchServerByID(Integer.parseInt(placeholder[0]))).getTextChanel(placeholder[1]).getChat();
        return chat;
    }
    private int  searchServerByID(int id) {
        synchronized (servers) {
            for(int i = 0; i < servers.size(); i++)
                if(servers.get(i).getServerID() == id)
                    return i;
        }
        return -1;
    }
    private void reset() { //TODO : USE IT IF Problem occurred
        synchronized (response) {
            try {
                response.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void sendMessage(NewMessageRequest request) throws IOException {
        Message newMessage = request.getMessage();
        if(request.getPlaceholder().length == 1) {
            User receiver = searchUser(request.getPlaceholder()[0]);
            userData.get(servingUser).getPrivateChat(receiver.getUsername()).addMessage(newMessage);
            userData.get(receiver).getPrivateChat(servingUser.getUsername()).addMessage(newMessage);
            sendNotification("New message from " + servingUser.getUsername(), receiver.getUsername());
        }
        else {
            int serverID = Integer.parseInt(request.getPlaceholder()[0]);
            SocialServer socialServer = servers.get(searchServerByID(serverID));
            TextChanel chanel = socialServer.getTextChanel(request.getPlaceholder()[1]);
            chanel.getChat().addMessage(newMessage);
            if(newMessage instanceof TextMessage) {
                String[] mentionedUsers = ((TextMessage) newMessage).getMentionedUsers();
                HashSet<String> qualifiedUsers = qualifyServerMembersToGetNotification(socialServer, chanel);
                for(String user : mentionedUsers)
                    if(qualifiedUsers.contains(user))
                        sendNotification(servingUser.getUsername() + "mentioned you : " + socialServer.getServerName(), user);
            }
        }
        response.writeObject(new NewMessageResponse(true));
    }
    private void react(ReactRequest request) throws IOException {
        String time = request.getTime();
        if(request.getPlaceholder().length == 1) {
            User receiver = searchUser(request.getPlaceholder()[0]);
            userData.get(servingUser).getPrivateChat(receiver.getUsername()).addReaction(servingUser.getUsername(), time, request.getReaction());
            userData.get(receiver).getPrivateChat(servingUser.getUsername()).addReaction(servingUser.getUsername(), time, request.getReaction());
        }
        else {
            int serverID = Integer.parseInt(request.getPlaceholder()[0]);
            SocialServer socialServer = servers.get(searchServerByID(serverID));
            TextChanel chanel = socialServer.getTextChanel(request.getPlaceholder()[1]);
            chanel.getChat().addReaction(servingUser.getUsername(), time, request.getReaction());
        }
        response.writeObject(new ReactResponse(true));
    }
    private void logOut() {} //TODO : log out
    private void login(LoginRequest info) throws IOException {
        String username, password;
        username = info.getUsername();
        password = info.getPassword();
        User loginClient = searchUser(username);
        if(loginClient == null || !userData.get(loginClient).getPassword().equals(password))
            response.writeObject(new LoginResponse(LoginStatus.FAILURE, null));
        else {
                loginClient.setStatus(UserStatus.ONLINE);
                response.writeObject(new LoginResponse(LoginStatus.SUCCESS, loginClient));
                servingUser = loginClient;
                if(onlineUsers.containsKey(username))
                    onlineUsers.get(username).add(this);
                else {
                    Vector<ClientHandler> clients = new Vector<>();
                    clients.add(this);
                    onlineUsers.put(username, clients);
                }
        }
    } //Done
    private void signUP(SignUpRequest info) throws IOException {
        String username , password , mail, phoneNumber;
        username = info.getUsername();
        password = info.getPassword();
        mail = info.getMail();
        phoneNumber = info.getPhoneNumber();
            SignUpStatus status = checkRegex(username, password, mail, phoneNumber);
            User newUser = null;
            if(status == SignUpStatus.VALID) {
                newUser = new User(username, mail, phoneNumber);
                userData.put(newUser, new UserData(password));
                servingUser = newUser;
                if(onlineUsers.containsKey(username))
                    onlineUsers.get(username).add(this);
                else {
                    Vector<ClientHandler> clients = new Vector<>();
                    clients.add(this);
                    onlineUsers.put(username, clients);
                }
                servingUser.setStatus(UserStatus.ONLINE);
            }
            response.writeObject(new SignUpResponse(status, newUser ));
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
        for(User user : userData.keySet())
            if(user.getUsername().equals(username))
                return user;
        return null;
    } //Done
    private  void getNotification(String notification) {
        synchronized (response) {
            try {
                response.writeObject(new Notification(notification));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    } //Done
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
            System.out.println("Couldn't close resources");
        }
    }
}
