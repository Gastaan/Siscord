package shared.requests;

public class ServerMemberRequest extends Request{
    private final int serverID;
    private final String name;
    //constructor
    public ServerMemberRequest(RequestType type, int serverID, String name) {
        super(type);
        this.serverID = serverID;
        this.name = name;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
    public String getName() {
        return name;
    }
}
