package shared.requests;

public class AddFriendToServerRequest extends Request{
    private final int serverID;
    private final String friendName;
    //constructor
    public AddFriendToServerRequest(int serverID, String friendName) {
        super(RequestType.ADD_FRIEND_TO_SERVER);
        this.serverID = serverID;
        this.friendName = friendName;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
    public String getFriendName() {
        return friendName;
    }
}
