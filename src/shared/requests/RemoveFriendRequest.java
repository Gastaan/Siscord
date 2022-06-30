package shared.requests;

public class RemoveFriendRequest extends Request{
    private final String requestingUser;
    private final String requestedUser;
    //constructor
    public RemoveFriendRequest(String requestingUser, String requestedUser) {
        super(ReqType.REMOVE_FRIEND);
        this.requestingUser = requestingUser;
        this.requestedUser = requestedUser;
    }
    //getters
    public String getRequestingUser() {
        return requestingUser;
    }
    public String getRequestedUser() {
        return requestedUser;
    }
}
