package shared.requests;

public class GetOutGoingFriendRequest extends Request{
    private final String requestedUser;
    //constructor
    public GetOutGoingFriendRequest(String requestedUser) {
        super(ReqType.GET_OUTGOING_FRIEND);
        this.requestedUser = requestedUser;
    }
    //getters
    public String getRequestedUser() {
        return requestedUser;
    }
}
