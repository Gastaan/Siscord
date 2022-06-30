package shared.requests;

public class AddFriendRequest extends Request{
    private final String requestingUser;
    private final String requestedUser;
    //constructor
    public AddFriendRequest(String requestingUser, String requestedUser) {
        super(ReqType.ADD_FRIEND);
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
