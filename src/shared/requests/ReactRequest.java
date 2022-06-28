package shared.requests;

import shared.user.User;
import shared.user.data.message.Message;
import shared.user.data.message.Reacts;

public class ReactRequest extends Request{
    private final User requestingUser;
    private final String requestedUsername;
    private final Message message;
    private final Reacts react;
    //constructor
    public ReactRequest(User requestingUser, String requestedUsername, Message message, Reacts react) {
        super(ReqType.PRIVATE_CHAT_REACT);
        this.requestingUser = requestingUser;
        this.requestedUsername = requestedUsername;
        this.message = message;
        this.react = react;
    }
        //getters
    public User getRequestingUser() {
        return requestingUser;
    }
    public String getRequestedUsername() {
        return requestedUsername;
    }
    public Message getMessage() {
        return message;
    }
    public Reacts getReact() {
        return react;
    }
}
