package server.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class UserData implements Serializable {
    private final ConcurrentHashMap<String, Chat> privateChats;
    private final HashSet<String> blockedUsers;
    private final HashSet<String> incomingFriendRequests;
    private final HashSet<String> outgoingFriendRequests;
    private final HashSet<Integer>   servers;
    private final HashSet<String> friends;
    private String password;
    //constructor
    public UserData(String password) {
        privateChats = new ConcurrentHashMap<>();
        blockedUsers = new HashSet<>();
        incomingFriendRequests = new HashSet<>();
        outgoingFriendRequests = new HashSet<>();
        servers = new HashSet<>();
        friends = new HashSet<>();
        this.password = password;
    }
    //getters
    public ArrayList<String> getPrivateChatList() {
        ArrayList<String> list = new ArrayList<>();
        synchronized (privateChats) {
            for(String username : privateChats.keySet()) {
                if(!blockedUsers.contains(username)) {
                    list.add(username);
                }
            }
        }
        return list;
    }
    public void newPrivateChat(String username) {
        synchronized (privateChats) {
            privateChats.put(username, new Chat());
        }
    }
    public Chat getPrivateChat(String chatName) {
        if(privateChats.containsKey(chatName))
            return privateChats.get(chatName);
        return  null;
    }
    //getters
    public ArrayList<String> getBlockedUsers() {
        synchronized (blockedUsers) {
            return   new ArrayList<>(blockedUsers);
        }
    }
    public ArrayList<String> getIncomingFriendRequests() {
        synchronized (incomingFriendRequests) {
            return new ArrayList<>(incomingFriendRequests);
        }
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
    public ArrayList<String> getFriends() {
        synchronized (friends) {
            return new ArrayList<>(friends);
        }
    }
    public ArrayList<String> getOutgoingFriendRequests() {
        synchronized (outgoingFriendRequests) {
            return new ArrayList<>(outgoingFriendRequests);
        }
    }
    public void unblockUser(String username) {
        synchronized (blockedUsers) {
            blockedUsers.remove(username);
        }
    }
    public void blockUser(String username) {
        synchronized (blockedUsers) {
            blockedUsers.add(username);
        }
    }
    public void addServer(int serverID) {
        synchronized (servers) {
            servers.add(serverID);
        }
    }
    public ArrayList<Integer> getServers() {
        synchronized (servers) {
            return new ArrayList<>(servers);
        }
    }
    public void changePassword(String password) {
        this.password = password;
    }
    public String getPassword() {
            return password;
    }
}
