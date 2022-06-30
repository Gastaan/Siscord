package shared.requests;

public class GetFriendRequestsRequest extends Request{
    private String requestedUser;
    //constructor
    public GetFriendRequestsRequest(String requestedUser) {
        super(ReqType.GET_FRIEND_REQUESTS);
        this.requestedUser = requestedUser;
    }
    //getters
    public String getRequestedUser() {
        return requestedUser;
    }
}
