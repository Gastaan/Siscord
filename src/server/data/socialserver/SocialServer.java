package server.data.socialserver;

import server.data.socialserver.chanel.Chanel;
import server.data.socialserver.chanel.TextChanel;
import server.data.socialserver.chanel.VoiceChanel;

import java.util.ArrayList;
import java.util.HashMap;
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
        roles.put(Roles.BLOCK_MEMBER, new HashSet<>());
        roles.put(Roles.CREATE_CHANEL, new HashSet<>());
        roles.put(Roles.DELETE_CHANEL, new HashSet<>());
        roles.put(Roles.KICK_MEMBER, new HashSet<>());
        roles.put(Roles.LIMIT_MEMBERS, new HashSet<>());
        roles.put(Roles.CHANGE_SERVERNAME, new HashSet<>());
        roles.put(Roles.PIN_MESSAGE, new HashSet<>());
        roles.get(Roles.CREATE_CHANEL).add(serverOwner);
        roles.get(Roles.DELETE_CHANEL).add(serverOwner);
        roles.get(Roles.KICK_MEMBER).add(serverOwner);
        roles.get(Roles.LIMIT_MEMBERS).add(serverOwner);
        roles.get(Roles.BLOCK_MEMBER).add(serverOwner);
        roles.get(Roles.CHANGE_SERVERNAME).add(serverOwner);
        roles.get(Roles.PIN_MESSAGE).add(serverOwner);
        members.put(serverOwner, new HashSet<>());
        members.get(serverOwner).add(Roles.CREATE_CHANEL);
        members.get(serverOwner).add(Roles.DELETE_CHANEL);
        members.get(serverOwner).add(Roles.KICK_MEMBER);
        members.get(serverOwner).add(Roles.LIMIT_MEMBERS);
        members.get(serverOwner).add(Roles.BLOCK_MEMBER);
        members.get(serverOwner).add(Roles.CHANGE_SERVERNAME);
        members.get(serverOwner).add(Roles.PIN_MESSAGE);
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
    public boolean addMember(String username) {
        if(members.containsKey(username) || blockedUsers.contains(username))
            return false;
        members.put(username, new HashSet<>());
        return true;
    }
    public void giveRole(String username, Roles role) {
        members.get(username).add(role);
        roles.get(role).add(username);
    }
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public int getServerID() {
        return serverID;
    }
    public HashMap<String, Boolean> getChanels() {
        HashMap<String, Boolean> list = new HashMap<>();
        synchronized (chanels) {
            for (String chanelName : chanels.keySet()) {
                list.put(chanelName, chanels.get(chanelName) instanceof TextChanel);
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
    public HashMap<String, HashSet<Roles>> getMembers() {
        synchronized (members) {
            return  new HashMap<>(members);
        }
    }
    public HashSet<String> getMembersUsername() {
        return new HashSet<>(members.keySet());
    }
    public boolean checkPermission(String username, Roles role) {
        return members.get(username).contains(role);
    }
    public ArrayList<String> getChanelNames() {
        ArrayList<String> list;
        synchronized (chanels) {
            list = new ArrayList<>(chanels.keySet());
        }
        return list;
    }
    public void createChanel(String chanelName, boolean isText) {
        if(isText)
            chanels.put(chanelName, new TextChanel());
        else
            chanels.put(chanelName, new VoiceChanel());
    }
    public void deleteChanel(String chanelName) {
        synchronized (chanels) {
            chanels.remove(chanelName);
        }
    }
    public void kickMember(String username) {
       synchronized (members) {
           synchronized (members) {
               for(Roles role : members.get(username))
                   roles.get(role).remove(username);
           }
           members.remove(username);
       }
    }
    public void blockMember(String username) {
        kickMember(username);
        blockedUsers.add(username);
    }
    public String getServerOwner() {
        return serverOwner;
    }
}
