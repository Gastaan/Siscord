package shared.requests;

import shared.user.User;

public class IsTypingRequest extends Request {
    private final User requestingUser;
    private final String requestedUsername;
    //constructor
    public IsTypingRequest(User requestingUser, String requestedUsername) {
        super(ReqType.PRIVATE_CHAT_IS_TYPING);
        this.requestingUser = requestingUser;
        this.requestedUsername = requestedUsername;
    }
    //getters
    public User getRequestingUser() {
        return requestingUser;
    }
    public String getRequestedUsername() {
        return requestedUsername;
    }
}
