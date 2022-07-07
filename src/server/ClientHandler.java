package server;


import server.data.Chat;
import server.data.UserData;
import server.data.socialserver.Roles;
import server.data.socialserver.SocialServer;
import server.data.socialserver.chanel.Chanel;
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
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
    private void giveResponse(Request requested) {
        if(requested.getType() == ReqType.LOGIN)
            login((LoginRequest) requested);
        else if(requested.getType() == ReqType.SIGN_UP)
            signUP((SignUpRequest)requested);
        else if(requested.getType() == ReqType.PRIVATE_CHAT_LIST)
            privateChatList();
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
            serverList();
        else if(requested.getType() == ReqType.SERVER_CHANELS)
            serverChanels((GetChanelsRequest) requested);
        else if(requested.getType() == ReqType.IS_TYPING)
            isTyping((IsTypingRequest) requested);
        else if(requested.getType() == ReqType.CHANGE_PASSWORD)
            changePassword((ChangePasswordRequest) requested);
    }
    private void changePassword(ChangePasswordRequest requested) {
         if(match(requested.getNewPassword(), passwordRegex)) {
             userData.get(servingUser).changePassword(requested.getNewPassword());
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
        String[] placeholder = requested.getPlaceholder();
      if(placeholder.length == 1)
          for(ClientHandler ch : onlineUsers.get(placeholder[0]))
              ch.sendNotification(servingUser.getUsername() + "is typing...");
      else {
          SocialServer notifyingServer = servers.get(searchServerByID(Integer.parseInt(placeholder[0])));
          Chanel textChanel = notifyingServer.getTextChanel(placeholder[1]);
          if(textChanel.getIsLimited()) {
              HashSet<String> accessUsers = textChanel.getAccessList();
              accessUsers.addAll(notifyingServer.getRoles(Roles.LIMIT_MEMBERS));
              for(String user : accessUsers)
                  for(ClientHandler ch : onlineUsers.get(user))
                      ch.sendNotification(servingUser.getUsername() + "is typing...");
          }
          else {
              for(String member : notifyingServer.getMembers())
                  for(ClientHandler ch : onlineUsers.get(member))
                      ch.sendNotification(servingUser.getUsername() + "is typing...");
          }
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
            if(onlineUsers.get(requestedFriend.getUsername()) != null)
                for(ClientHandler ch : onlineUsers.get(requestedFriend.getUsername()))
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
        if(onlineUsers.get(requestedUser.getUsername()) != null)
            for(ClientHandler ch : onlineUsers.get(requestedUser.getUsername()))
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
            if(onlineUsers.get(wantedUser.getUsername()) != null)
                for(ClientHandler ch : onlineUsers.get(wantedUser.getUsername()))
                    ch.sendNotification("New private chat with " + servingUser.getUsername() + " just created!");
        }
        try {
            response.writeObject(new NewPrivateChatResponse(status));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void privateChatList() {
        ArrayList<String> chatNames = userData.get(servingUser).getPrivateChatList();
        try {
            response.writeObject(new ChatListResponse(chatNames));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void chat(ChatRequest request) {
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
        if(loginClient == null || !userData.get(loginClient).getPassword().equals(password)) {
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
                if(onlineUsers.containsKey(username))
                    onlineUsers.get(username).add(this);
                else {
                    Vector<ClientHandler> clients = new Vector<>();
                    clients.add(this);
                    onlineUsers.put(username, clients);
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
                userData.put(newUser, new UserData(password));
                servingUser = newUser;
                if(onlineUsers.containsKey(username))
                    onlineUsers.get(username).add(this);
                else {
                    Vector<ClientHandler> clients = new Vector<>();
                    clients.add(this);
                    onlineUsers.put(username, clients);
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
        for(User user : userData.keySet())
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
