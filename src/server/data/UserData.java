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
        synchronized (privateChats) {
            chatNames.addAll(privateChats.keySet());
        }
        return chatNames;
    }
    public PrivateChat getPrivateChat(String chatName) {
        return privateChats.get(chatName);
    }
    //getters
    public HashSet<String> getBlockedUsers() {
        return blockedUsers;
    }
    public HashSet<String> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }
    public HashSet<String> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    }
    public void addOutgoingFriendRequest(String username) {
        synchronized (outgoingFriendRequests) {
            outgoingFriendRequests.add(username);
        }
    }
    public void deleteOutgoingFriendRequest(String username) {
        synchronized (outgoingFriendRequests) {
            outgoingFriendRequests.remove(username);
        }
    }
    public void addIncomingFriendRequest(String username) {
        synchronized (incomingFriendRequests) {
            incomingFriendRequests.add(username);
        }
    }
    public void deleteIncomingFriendRequest(String username) {
        synchronized (incomingFriendRequests) {
            incomingFriendRequests.remove(username);
        }
    }
    public void addFriend(String username) {
        synchronized (friends) {
            friends.add(username);
        }
    }
    public void deleteFriend(String username) {
        synchronized (friends) {
            friends.remove(username);
        }
    }
    public boolean isFriend(String username) {
        synchronized (friends) {
            return friends.contains(username);
        }
    }
}
