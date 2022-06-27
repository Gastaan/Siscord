package server.data;

import socialserver.SocialServer;
import user.User;
import user.data.message.Message;

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
    //costructor
    public UserData() {
        privateChats = new HashMap<>();
        blockedUsers = new HashSet<>();
        incomingFriendRequests = new HashSet<>();
        outgoingFriendRequests = new HashSet<>();
        servers = new HashSet<>();
        friends = new HashMap<>();
    }

}
