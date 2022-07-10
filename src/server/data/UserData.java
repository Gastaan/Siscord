package server.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author saman hazemi
 * This class is used to store the data of a user.
 */
public class UserData implements Serializable {
    private final ConcurrentHashMap<String, Chat> privateChats;
    private final HashSet<String> blockedUsers;
    private final HashSet<String> incomingFriendRequests;
    private final HashSet<String> outgoingFriendRequests;
    private final HashSet<Integer>   servers;
    private final HashSet<String> friends;
    private String password;
    //constructor

    /**
     * Constructor for the class.
     * @param password The password of the user.
     */
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

    /**
     * Getter for private chats.
     * @return The private chats.
     */
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

    /**
     * @param username The new chat partner.
     */
    public void newPrivateChat(String username) {
        synchronized (privateChats) {
            privateChats.put(username, new Chat());
        }
    }

    /**
     * @param chatName The name of the chat (username of the chat partner).
     * @return The chat.
     */
    public Chat getPrivateChat(String chatName) {
        if(privateChats.containsKey(chatName))
            return privateChats.get(chatName);
        return  null;
    }
    //getters
    /**
     * @return The incoming friend requests.
     */
    public ArrayList<String> getIncomingFriendRequests() {
        synchronized (incomingFriendRequests) {
            return new ArrayList<>(incomingFriendRequests);
        }
    }

    /**
     * This method is used to add a friend request to the outgoing friend requests.
     * @param username The username of the user that request sent to.
     */
    public void addOutgoingFriendRequest(String username) {
        synchronized (outgoingFriendRequests) {
            outgoingFriendRequests.add(username);
        }
    }

    /**
     * This method is used to remove a friend request from the outgoing friend requests.
     * @param username The username of the user that request sent to.
     */
    public void deleteOutgoingFriendRequest(String username) {
        synchronized (outgoingFriendRequests) {
            outgoingFriendRequests.remove(username);
        }
    }
    /**
     * @return The outgoing friend requests.
     */
    public ArrayList<String> getOutgoingFriendRequests() {
        synchronized (outgoingFriendRequests) {
            return new ArrayList<>(outgoingFriendRequests);
        }
    }
    /**
     * This method is used to add a friend request to the incoming friend requests.
     * @param username The username of the user that sent the request.
     */
    public void addIncomingFriendRequest(String username) {
        synchronized (incomingFriendRequests) {
            incomingFriendRequests.add(username);
        }
    }

    /**
     * This method is used to remove a friend request from the incoming friend requests.
     * @param username The username of the user that sent the request.
     */
    public void deleteIncomingFriendRequest(String username) {
        synchronized (incomingFriendRequests) {
            incomingFriendRequests.remove(username);
        }
    }

    /**
     * This method is used to add a friend to the friends list.
     * @param username The username of the friend.
     */
    public void addFriend(String username) {
        synchronized (friends) {
            friends.add(username);
        }
    }

    /**
     * This method is used to remove a friend from the friends list.
     * @param username The username of the friend.
     */
    public void deleteFriend(String username) {
        synchronized (friends) {
            friends.remove(username);
        }
    }

    /**
     * This method checks if the user is a friend of the other user.
     * @param username The username of the user.
     * @return Whether the user is a friend of the other user.
     */
    public boolean isFriend(String username) {
        synchronized (friends) {
            return friends.contains(username);
        }
    }

    /**
     * @return The friends list.
     */
    public ArrayList<String> getFriends() {
        synchronized (friends) {
            return new ArrayList<>(friends);
        }
    }

    /**
     * @param username The username of the user that is being unblocked.
     */
    public void unblockUser(String username) {
        synchronized (blockedUsers) {
            blockedUsers.remove(username);
        }
    }

    /**
     * @param username The username of the user that is being blocked.
     */
    public void blockUser(String username) {
        synchronized (blockedUsers) {
            blockedUsers.add(username);
        }
    }

    /**
     * Add a server to the servers list.
     * @param serverID The server ID.
     */

    /**
     * Getter for the blocked users.
     * @return The blocked users.
     */
    public ArrayList<String> getBlockedUsers() {
        synchronized (blockedUsers) {
            return   new ArrayList<>(blockedUsers);
        }
    }
    public void addServer(int serverID) {
        synchronized (servers) {
            servers.add(serverID);
        }
    }
    public void deleteServer(int serverID) {
        synchronized (servers) {
            servers.remove(serverID);
        }
    }
    /**
     * @return The servers list.
     */
    public ArrayList<Integer> getServers() {
        synchronized (servers) {
            return new ArrayList<>(servers);
        }
    }

    /**
     * This method is used to change the password of the user.
     * @param password new password.
     */
    public void changePassword(String password) {
        this.password = password;
    }

    /**
     * @return The password of the user.
     */
    public String getPassword() {
            return password;
    }
}
