package shared.requests;

public class AddFriendRequest extends Request {
    private final String requestedUser;
    //constructor
    public AddFriendRequest( String requestedUser) {
        super(ReqType.ADD_FRIEND);
        this.requestedUser = requestedUser;
    }
    //getters
    public String getRequestedUser() {
        return requestedUser;
    }
}
