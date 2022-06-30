package shared.requests;

public class FriendRequestAnswerRequest extends Request{
    private final String requestingUser;
    private final String requestedUser;
    private final boolean accept;
    //constructor
    public FriendRequestAnswerRequest(String requestingUser, String requestedUser, boolean accept) {
        super(ReqType.FRIEND_REQUEST_ANSWER);
        this.requestingUser = requestingUser;
        this.requestedUser = requestedUser;
        this.accept = accept;
    }
    //getters
    public String getRequestingUser() {
        return requestingUser;
    }
    public String getRequestedUser() {
        return requestedUser;
    }
    public boolean isAccept() {
        return accept;
    }
}
