package shared.requests;

import shared.user.User;
import shared.user.data.message.Message;
import shared.user.data.message.Reacts;

public class ReactRequest extends Request{
    private final User requestedUser;
    private final String username;
    private final Message message;
    private final Reacts react;
    //constructor
    public ReactRequest(User requestedUser, String username, Message message, Reacts react) {
        super(ReqType.REACT);
        this.requestedUser = requestedUser;
        this.username = username;
        this.message = message;
        this.react = react;
    }
        //getters
    public User getRequestedUser() {
        return requestedUser;
    }
    public String getUsername() {
        return username;
    }
    public Message getMessage() {
        return message;
    }
    public Reacts getReact() {
        return react;
    }
}
