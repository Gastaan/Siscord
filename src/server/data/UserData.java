package server.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class UserData implements Serializable {
    private final ConcurrentHashMap<String, PrivateChat> privateChats;
    private final HashSet<String> blockedUsers;
    private final HashSet<String> incomingFriendRequests;
    private final HashSet<String> outgoingFriendRequests;
    private final HashSet<String>   servers;
    private final HashSet<String> friends;
    //constructor
    public UserData() {
        privateChats = new ConcurrentHashMap<>();
        blockedUsers = new HashSet<>();
        incomingFriendRequests = new HashSet<>();
        outgoingFriendRequests = new HashSet<>();
        servers = new HashSet<>();
        friends = new HashSet<>();
    }
    //getters
    public ArrayList<String> getPrivateChatList() {
        ArrayList<String> chatNames = new ArrayList<>();
        chatNames.addAll(privateChats.keySet());
        return chatNames;
    }
}
