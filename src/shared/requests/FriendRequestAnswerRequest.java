package shared.requests;

/**
 * This class is used to send a request to the server to accept or reject a friend request.
 */
public class FriendRequestAnswerRequest extends Request{
    private final String requestedUser;
    private final boolean accept;
    //constructor

    /**
     * Constructor for the class.
     * @param requestedUser The user that sent the friend request.
     * @param accept Whether the request is accepted or rejected.
     */
    public FriendRequestAnswerRequest(String requestedUser, boolean accept) {
        super(ReqType.FRIEND_REQUEST_ANSWER);
        this.requestedUser = requestedUser;
        this.accept = accept;
    }
    //getters

    /**
     * Getter for the requested user.
     * @return The requested user.
     */
    public String getRequestedUser() {
        return requestedUser;
    }

    /**
     * Getter for the accept value.
     * @return Whether the request is accepted or rejected.
     */
    public boolean isAccept() {
        return accept;
    }
}
