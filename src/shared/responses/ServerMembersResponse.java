package shared.responses;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerMembersResponse extends Response{
    private final int serverId;
    private final HashMap<String, String> members;
    //constructor
    public ServerMembersResponse(int serverId, HashMap<String, String> members) {
        super(ResponseType.SERVER_MEMBERS);
        this.serverId = serverId;
        this.members = members;
    }
    //getters
    public int getServerId() {
        return serverId;
    }
    public ArrayList<String> getMembers() {
        return new ArrayList<>(members.keySet());
    }
    //toString
    @Override
    public String toString() {
        String value = "Members: \n";
        for(String member : members.keySet())
            value += member + " : " + members.get(member) + "\n";
        return value;
    }
}
