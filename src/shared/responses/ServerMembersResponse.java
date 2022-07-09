package shared.responses;

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
    //toString
    @Override
    public String toString() {
        return "ServerMembersResponse{" +
                "serverId=" + serverId +
                ", members=" + members +
                '}';
    }
}
