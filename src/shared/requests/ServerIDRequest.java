package shared.requests;

public class ServerIDRequest extends Request{
    private final int serverID;
    //constructor
    public ServerIDRequest(RequestType type, int serverID) {
        super(type);
        this.serverID = serverID;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
}
