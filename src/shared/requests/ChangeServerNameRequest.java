package shared.requests;

public class ChangeServerNameRequest extends Request{
    private final int serverID;
    private final String newName;
    //constructor
    public ChangeServerNameRequest(int serverID, String newName) {
        super(RequestType.CHANGE_SERVER_NAME);
        this.serverID = serverID;
        this.newName = newName;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
    public String getNewName() {
        return newName;
    }
}
