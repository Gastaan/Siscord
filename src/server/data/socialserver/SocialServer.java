package server.data.socialserver;

import server.data.socialserver.chanel.Chanel;
import server.data.socialserver.chanel.TextChanel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class SocialServer { //TODO : welcome message , delete server
    private String serverName;
    private final Integer serverID;
    private final String serverOwner;
    private final Vector<String> blockedUsers;
    private final ConcurrentHashMap<String, HashSet<Roles>> members;
    private final ConcurrentHashMap<Roles, HashSet<String>> roles;
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
        this.roles = new ConcurrentHashMap<>();
        roles.put(Roles.BLOCK_USER, new HashSet<>());
        roles.put(Roles.CREATE_CHANEL, new HashSet<>());
        roles.put(Roles.DELETE_CHANEL, new HashSet<>());
        roles.put(Roles.DELETE_MEMBER, new HashSet<>());
        roles.put(Roles.LIMIT_MEMBERS, new HashSet<>());
        roles.put(Roles.CHANGE_SERVERNAME, new HashSet<>());
        roles.put(Roles.PIN_MESSAGE, new HashSet<>());
        roles.get(Roles.CREATE_CHANEL).add(serverOwner);
        roles.get(Roles.DELETE_CHANEL).add(serverOwner);
        roles.get(Roles.DELETE_MEMBER).add(serverOwner);
        roles.get(Roles.LIMIT_MEMBERS).add(serverOwner);
        roles.get(Roles.BLOCK_USER).add(serverOwner);
        roles.get(Roles.CHANGE_SERVERNAME).add(serverOwner);
        roles.get(Roles.PIN_MESSAGE).add(serverOwner);
    }
    public HashSet<String> getRoles(Roles role) {
        synchronized (roles) {
            return new HashSet<>(roles.get(role));
        }
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
    public TextChanel getTextChanel(String chanelName) {
        if(chanels.containsKey(chanelName)) {
            return (TextChanel)chanels.get(chanelName);
        }
        return null;
    }
    public HashSet<String> getMembers() {
        return new HashSet<>(members.keySet());
    }
}
