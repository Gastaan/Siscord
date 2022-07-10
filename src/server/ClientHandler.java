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
import shared.responses.ListResponse;
import shared.responses.LoginResponse;
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
    private final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$";
    private final String mailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private final String phoneNumberRegex = "^(\\+98|0)?9\\d{9}$";

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
                reset();
                requested = (Request) request.readObject();
                giveResponse(requested);
            }
        }
        catch (Exception e) {
            System.out.println("Client disconnected!");
            e.printStackTrace();
            try {
                logOut();
            } catch (IOException ex) {
                System.out.println("Could not log out!");
            }
            close();
        }
    }

    /**
     * This method gives the response to the client.
     * @param requested the request from the client.
     */
    private void giveResponse(Request requested) throws IOException {
        RequestType requestType = requested.getType();
        if(requestType == RequestType.LOGIN)
            login((LoginRequest) requested);
        else if(requestType == RequestType.SIGN_UP)
            signUP((SignUpRequest)requested);
        else if(requestType == RequestType.PRIVATE_CHAT_LIST)
            privateChatList();
        else if(requestType == RequestType.CHAT_REQUEST)
            chat((PlaceholderRequest) requested);
        else if(requestType == RequestType.CHAT_REACT)
            react((ReactRequest) requested);
        else if(requestType == RequestType.NEW_PRIVATE_CHAT)
            newPrivateChat((StringRequest) requested);
        else if(requestType == RequestType.NEW_MESSAGE)
            sendMessage((NewMessageRequest) requested);
        else if(requestType == RequestType.GET_INCOMING_FRIEND_REQUESTS)
            getFriendRequests();
        else if(requestType == RequestType.FRIEND_REQUEST_ANSWER)
            friendRequestAnswer((FriendRequestAnswerRequest) requested);
        else if(requestType == RequestType.ADD_FRIEND)
            addFriend((StringRequest) requested);
        else if(requestType == RequestType.GET_FRIENDS_LIST)
            getFriendsList();
        else if(requestType == RequestType.REMOVE_FRIEND)
            removeFriend((StringRequest) requested);
        else if(requestType == RequestType.GET_OUTGOING_FRIEND)
            getOutgoingFriend();
        else if(requestType == RequestType.GET_BLOCKED_USERS)
            getBlockedUsers();
        else if(requestType == RequestType.UNBLOCK_USER)
            unblockUser((StringRequest)requested);
        else if (requestType == RequestType.BLOCK_USER)
            blockUser((StringRequest)requested);
        else if(requestType == RequestType.NEW_SERVER)
            newServer((StringRequest) requested);
        else if(requestType == RequestType.SERVER_LIST)
            serverList();
        else if(requestType == RequestType.SERVER_CHANELS)
            serverChanels((ServerIDRequest) requested);
        else if(requestType == RequestType.IS_TYPING)
            isTyping((PlaceholderRequest) requested);
        else if(requestType == RequestType.CHANGE_PASSWORD)
            changePassword((StringRequest) requested);
        else if(requestType == RequestType.PIN_MESSAGE)
            pin((PinRequest) requested);
        else if(requestType == RequestType.CANCEL_FRIEND_REQUEST)
            cancelFriendRequest((StringRequest) requested);
        else if(requestType == RequestType.CHANGE_EMAIL)
            changeEmail((StringRequest) requested);
        else if(requestType == RequestType.CHANGE_PHONE_NUMBER)
            changePhoneNumber((StringRequest) requested);
        else if(requestType == RequestType.CREATE_CHANEL)
            createChanel((CreateChanelRequest) requested);
        else if(requestType == RequestType.DELETE_CHANEL)
            deleteChanel((PlaceholderRequest) requested);
        else if(requestType == RequestType.ADD_FRIEND_TO_SERVER)
            addFriendToServer((ServerMemberRequest) requested);
        else if(requestType == RequestType.SERVER_MEMBERS)
            serverMembers((ServerIDRequest) requested);
        else if(requestType == RequestType.KICK_MEMBER)
            kickMember((ServerMemberRequest) requested);
        else if(requestType == RequestType.BLOCK_MEMBER)
            blockMember((ServerMemberRequest) requested);
        else if(requestType == RequestType.GIVE_ROLE)
            giveRole((ServerMemberRequest) requested);
        else if(requestType == RequestType.CHANGE_STATUS)
            changeStatus((StringRequest) requested);
        else if(requestType == RequestType.LOGOUT)
            logOut();
        else if(requestType == RequestType.VOICE)
            sendVoiceCall((Voice) requested);
        else if(requestType == RequestType.CHANGE_SERVER_NAME)
               changeServerName((ChangeServerNameRequest) requested);
        else if(requestType == RequestType.LEAVE_SERVER)
                leaveServer((ServerIDRequest) requested);
    }
    private void leaveServer(ServerIDRequest requested) throws IOException {
        SocialServer server = servers.get(searchServerByID(requested.getServerID()));
        boolean success = false;
        if(!server.getServerOwner().equals(servingUser.getUsername())) {
            success = true;
            server.kickMember(servingUser.getUsername());
            userData.get(servingUser).deleteServer(requested.getServerID());
        }
        response.writeObject(new BooleanResponse(ResponseType.LEAVE_SERVER, success));
    }
    private void changeServerName(ChangeServerNameRequest request) throws IOException {
        SocialServer server = servers.get(searchServerByID(request.getServerID()));
        boolean success = false;
        if(server.checkPermission(servingUser.getUsername(), Roles.CHANGE_SERVERNAME)) {
            success = true;
            server.setServerName(request.getNewName());
        }
        response.writeObject(new BooleanResponse(ResponseType.CHANGE_SERVER_NAME, success));
    }
    private void sendVoiceCall(Voice requested) throws IOException {
        String receiver = requested.getUsername();
        VoiceResponse response = new VoiceResponse(requested.getBufferedVoice(), requested.getBytesRead(), servingUser.getUsername());
        for(ClientHandler client : onlineUsers.get(receiver)) {
            client.getVoiceCall(response);
        }
    }
    private void getVoiceCall(VoiceResponse voice) throws IOException {
        synchronized (response) {
            response.writeObject(voice);
            reset();
        }
    }
    private UserStatus findStatus(String status) {
        return switch (status) {
            case "1" -> UserStatus.ONLINE;
            case "2" -> UserStatus.IDLE;
            case "3" -> UserStatus.DO_NOT_DISTURB;
            case "4" -> UserStatus.OFFLINE;
            default -> null;
        };
    }
    private void changeStatus(StringRequest requested) throws IOException {
        String status = requested.getValue();
        servingUser.setStatus(findStatus(status));
        response.writeObject(new Response(ResponseType.STATUS_CHANGED));
    }
    private Roles getRole(int roleIndex) {
        return switch (roleIndex) {
            case 1 -> Roles.CREATE_CHANEL;
            case 2 -> Roles.DELETE_CHANEL;
            case 3 -> Roles.KICK_MEMBER;
            case 4 -> Roles.LIMIT_MEMBERS;
            case 5 -> Roles.BLOCK_MEMBER;
            case 6 -> Roles.CHANGE_SERVERNAME;
            case 7 -> Roles.PIN_MESSAGE;
            default -> null;
        };
    }
    private void giveRole(ServerMemberRequest requested) throws IOException {
        SocialServer socialServer = servers.get(searchServerByID(requested.getServerID()));
        boolean success = false;
        if(socialServer.getServerOwner().equals(servingUser.getUsername())) {
            success = true;
            socialServer.giveRole(requested.getName(), getRole(requested.getRoleIndex()));
        }
        response.writeObject(new BooleanResponse(ResponseType.GIVE_ROLE, success));
    }
    private void blockMember(ServerMemberRequest requested) throws IOException {
        SocialServer socialServer = servers.get(searchServerByID(requested.getServerID()));
        boolean success = false;
        if(socialServer.checkPermission(servingUser.getUsername(), Roles.BLOCK_MEMBER) && !socialServer.getServerOwner().equals(requested.getName())) {
            socialServer.blockMember(requested.getName());
            userData.get(searchUser(requested.getName())).deleteServer(requested.getServerID());
            success = true;
        }
        response.writeObject(new BooleanResponse(ResponseType.BLOCK_MEMBER, success));
    }
    private void kickMember(ServerMemberRequest request) throws IOException {
        SocialServer socialServer = servers.get(searchServerByID(request.getServerID()));
        boolean success = false;
        if(socialServer.checkPermission(servingUser.getUsername(), Roles.KICK_MEMBER) && !socialServer.getServerOwner().equals(request.getName())) {
            socialServer.kickMember(request.getName());
            userData.get(searchUser(request.getName())).deleteServer(request.getServerID());
            success = true;
        }
        response.writeObject(new BooleanResponse(ResponseType.KICK_MEMBER, success));
    }
    private void serverMembers(ServerIDRequest requested) throws IOException {
        SocialServer socialServer = servers.get(searchServerByID(requested.getServerID()));
        HashMap<String, String> members = new HashMap<>();
        for(String member : socialServer.getMembersUsername())
            members.put(member, searchUser(member).getStatus().toString());
        response.writeObject(new ServerMembersResponse(socialServer.getServerID(), members));
    }
    private void addFriendToServer(ServerMemberRequest requested) throws IOException {
        SocialServer socialServer = servers.get(searchServerByID(requested.getServerID()));
        String friendName = requested.getName();
        boolean success = socialServer.addMember(friendName);
        if(success)
            userData.get(searchUser(friendName)).addServer(socialServer.getServerID());
        response.writeObject(new BooleanResponse(ResponseType.ADD_FRIEND_TO_SERVER, success));

    }
    private void deleteChanel(PlaceholderRequest requested) throws IOException {
        SocialServer socialServer = servers.get(searchServerByID(Integer.parseInt(requested.getPlaceholder()[0])));
        String chanelName = requested.getPlaceholder()[1];
        boolean success = false;
        if(socialServer.checkPermission(servingUser.getUsername(), Roles.DELETE_CHANEL)) {
            socialServer.deleteChanel(chanelName);
            success = true;
        }
        response.writeObject(new BooleanResponse(ResponseType.DELETE_CHANEL, success));
    }
    /**
     * This method is used to create a new channel.
     * @param requested the request from the client.
     * @throws IOException if the client is disconnected.
     */
    private void createChanel(CreateChanelRequest requested) throws IOException {
        SocialServer socialServer = servers.get(searchServerByID(requested.getServerId()));
        String chanelName = requested.getChanelName();
        if(!socialServer.checkPermission(servingUser.getUsername(), Roles.CREATE_CHANEL))
            response.writeObject(new Response(ResponseType.PERMISSION_DENIED));
        else if(socialServer.getChanelNames().contains(chanelName))
            response.writeObject(new BooleanResponse(ResponseType.CREATE_CHANEL, false));
        else {
           response.writeObject(new BooleanResponse(ResponseType.CREATE_CHANEL, true));
           socialServer.createChanel(chanelName, requested.isTextChanel());
        }
    }
    private void changeEmail(StringRequest request) throws IOException {
        String email = request.getValue();
        boolean success = match(email, mailRegex);
        if(success)
            synchronized (servingUser) {
                servingUser.setEmail(email);
            }
        response.writeObject(new BooleanResponse(ResponseType.CHANGE_EMAIL, success));
    }
    private void changePhoneNumber(StringRequest request) throws IOException {
        String phoneNumber = request.getValue();
        boolean success = match(phoneNumber, phoneNumberRegex);
        if(success)
            synchronized (servingUser) {
                servingUser.setPhoneNumber(phoneNumber);
            }
        response.writeObject(new BooleanResponse(ResponseType.CHANGE_PHONE_NUMBER, success));
    }
    private void cancelFriendRequest(StringRequest requested) {
        userData.get(servingUser).deleteOutgoingFriendRequest(requested.getValue());
        userData.get(searchUser(requested.getValue())).deleteIncomingFriendRequest(servingUser.getUsername());
    }
    private void pin(PinRequest request) throws IOException {
        String[] placeholders = request.getPlaceHolder();
        boolean success = false;
        if(placeholders.length == 1) {
            User user =  searchUser(placeholders[0]);
            userData.get(servingUser).getPrivateChat(user.getUsername()).pinMessage(request.getTime());
            userData.get(user).getPrivateChat(servingUser.getUsername()).pinMessage(request.getTime());
            success = true;
        }
        else {
            int serverID = Integer.parseInt(placeholders[0]);
            SocialServer socialServer = servers.get(searchServerByID(serverID));
            if(socialServer.checkPermission(servingUser.getUsername(), Roles.PIN_MESSAGE)) {
                success = true;
                TextChanel chanel = socialServer.getTextChanel(placeholders[1]);
                chanel.getChat().pinMessage(request.getTime());
            }
        }
        response.writeObject(new BooleanResponse(ResponseType.PIN_MESSAGE, success));
    }

    /**
     * This method changes the password of the user.
     * @param requested the change password request from the client.
     */
    private void changePassword(StringRequest requested) throws IOException {
        boolean success = false;
         if(match(requested.getValue(), passwordRegex)) {
             userData.get(servingUser).changePassword(requested.getValue());
             success = true;
         }
        response.writeObject(new BooleanResponse(ResponseType.CHANGE_PASSWORD, success));
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
            return notifyingServer.getMembersUsername();
    }
    private void isTyping(PlaceholderRequest requested) throws IOException {
        String[] placeholder = requested.getPlaceholder();
      if(placeholder.length == 1)
              sendNotification(servingUser.getUsername() + "is typing...", placeholder[0]);
      else {
          SocialServer notifyingServer = servers.get(searchServerByID(Integer.parseInt(placeholder[0])));
          Chanel textChanel = notifyingServer.getTextChanel(placeholder[1]);
          for(String username : qualifyServerMembersToGetNotification(notifyingServer, textChanel))
              sendNotification(servingUser.getUsername() + "is typing...", username);
      }
      response.writeObject(new Response(ResponseType.IS_TYPING));
    }
    private void serverChanels(ServerIDRequest requested) throws IOException {
        SocialServer server = servers.get(searchServerByID(requested.getServerID()));
        HashMap<String, Boolean> chanels = server.getChanels();
        response.writeObject(new ChanelListResponse(requested.getServerID(), chanels));
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

    /**
     * This method is used to create a new server.
     * @param requested
     * @throws IOException
     */
    private void newServer(StringRequest requested) throws IOException {
        SocialServer server = new SocialServer(requested.getValue(), servingUser.getUsername());
        servers.add(server);
        userData.get(servingUser).addServer(server.getServerID());
        response.writeObject(new NewServerResponse(true, server.getServerID()));
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
        response.writeObject(new BooleanResponse(ResponseType.BLOCK_USER ,success));
    }
    private void unblockUser(StringRequest requested) throws IOException {
        userData.get(servingUser).unblockUser(requested.getValue());
        response.writeObject(new Response(ResponseType.UNBLOCK_USER));
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
        User requestedUser = servingUser;
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
        response.writeObject(new AddFriendResponse(status, requestedFriend == null ? null : requestedFriend.getUsername()));
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
    private void chat(PlaceholderRequest request) throws IOException {
        Chat chat = getChat(request.getPlaceholder());
        response.writeObject(new ChatResponse(chat.getMessages(), chat.getPinnedMessages(), request.getPlaceholder()));
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
        response.writeObject(new Response(ResponseType.MESSAGE_DELIVERED));
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
        response.writeObject(new Response(ResponseType.REACTED_TO_MESSAGE));
    }
    private void logOut() throws IOException {
        synchronized (onlineUsers) {
            onlineUsers.get(servingUser.getUsername()).remove(this);
            if(onlineUsers.get(servingUser.getUsername()).isEmpty()) {
                onlineUsers.remove(servingUser.getUsername());
                servingUser.setStatus(UserStatus.OFFLINE);
            }
            servingUser = null;
        }
        response.writeObject(new Response(ResponseType.LOGGED_OUT));
    }
    private void login(LoginRequest info) throws IOException {
        String username, password;
        username = info.getUsername();
        password = info.getPassword();
        User loginClient = searchUser(username);
        if(loginClient == null || !userData.get(loginClient).getPassword().equals(password))
            response.writeObject(new LoginResponse(false, null));
        else {
                loginClient.setStatus(UserStatus.ONLINE);
                response.writeObject(new LoginResponse(true, loginClient));
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
            String usernameRegex = "[a-zA-Z0-9]{6,}";
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
                reset();
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
