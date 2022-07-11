package shared.requests;

public class ServerMemberRequest extends Request{
    private final int serverID;
    private final String name;
    private int roleIndex;
    //constructor
    public ServerMemberRequest(RequestType type, int serverID, String name) {
        super(type);
        this.serverID = serverID;
        this.name = name;
    }
    public ServerMemberRequest(RequestType type, int serverID, String name, int roleIndex) {
        this(type, serverID, name);
        this.roleIndex = roleIndex;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
    public String getName() {
        return name;
    }
    public int getRoleIndex() {
        return roleIndex;
    }
}
