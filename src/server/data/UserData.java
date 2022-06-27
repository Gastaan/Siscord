package server.data;

import shared.socialserver.SocialServer;
import shared.user.User;
import shared.user.data.message.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UserData implements Serializable {
    private final HashMap<User, ArrayList<Message>> privateChats;
    private final HashSet<User> blockedUsers;
    private final HashSet<User> incomingFriendRequests;
    private final HashSet<User> outgoingFriendRequests;
    private final HashSet<SocialServer>   servers;
    private final HashMap<User, HashSet<User>> friends;
    //constructor
    public UserData() {
        privateChats = new HashMap<>();
        blockedUsers = new HashSet<>();
        incomingFriendRequests = new HashSet<>();
        outgoingFriendRequests = new HashSet<>();
        servers = new HashSet<>();
        friends = new HashMap<>();
    }
    //getters
    public HashMap<User, ArrayList<Message>> getPrivateChats() {
        return privateChats;
    }
    public HashSet<User> getBlockedUsers() {
        return blockedUsers;
    }
    public HashSet<User> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }
    public HashSet<User> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    }
    public HashSet<SocialServer> getServers() {
        return servers;
    }
    public HashMap<User, HashSet<User>> getFriends() {
        return friends;
    }
    public ArrayList<String> getPrivateChatList() {
        ArrayList<String> chatNames = new ArrayList<>();
        for(User chatName : privateChats.keySet()) {
            chatNames.add(chatName.getUsername());
        }
        return chatNames;
    }
}
