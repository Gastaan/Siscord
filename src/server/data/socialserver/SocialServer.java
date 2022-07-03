package server.data.socialserver;

import server.data.socialserver.chanel.Chanel;
import server.data.socialserver.chanel.TextChanel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

enum Roles{
    CREATE_CHANEL,
    DELETE_CHANEL,
    DELETE_MEMBER,
    LIMIT_MEMBERS,
    BLOCK_USER,
    CHANGE_SERVERNAME,
    PIN_MESSAGE
}
public class SocialServer {
    private String serverName;
    private final Integer serverID;
    private final String serverOwner;
    private final Vector<String> blockedUsers;
    private final ConcurrentHashMap<String, HashSet<Roles>> members;
    private final ConcurrentHashMap<String, Chanel> chanels;
    private static int serverIndex = 1;
    //constructor
    public SocialServer(String serverName, String serverOwner) {
        this.serverName = serverName;
        serverID = serverIndex++;
        this.serverOwner = serverOwner;
        this.blockedUsers = new Vector<>();
        this.members = new ConcurrentHashMap<>();
        this.chanels = new ConcurrentHashMap<>();
    }
    private void changeName(String newName) {
        serverName = newName;
    }
    private void blockUser(String username) {
        blockedUsers.add(username);
    }
    public void addMember(String username) {
        members.put(username, new HashSet<>());
    }
    public void giveRole(String username, Roles role) {
        members.get(username).add(role);
    }
    public String getServerName() {
        return serverName;
    }
    public int getServerID() {
        return serverID;
    }
    public ArrayList<String> getChanels() {
        ArrayList<String> list = new ArrayList<>();
        synchronized (chanels) {
            for (String chanelName : chanels.keySet()) {
                list.add((chanels.get(chanelName) instanceof TextChanel ? "Text Chanel :  " : "Voice Chanel : ") + chanelName);
            }
        }
        return list;
    }
}
