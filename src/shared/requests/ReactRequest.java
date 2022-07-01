package shared.requests;

import shared.user.data.message.Reacts;

public class ReactRequest extends Request{
    private final String chatName;
    private final String message;
    private final Reacts react;
    //constructor
    public ReactRequest(String chatName, String message, Reacts react) {
        super(ReqType.PRIVATE_CHAT_REACT);
        this.chatName = chatName;
        this.message = message;
        this.react = react;
    }
    //getters
    public String getChatName() {
        return chatName;
    }
    public String getMessage() {
        return message;
    }
    public Reacts getReact() {
        return react;
    }
}
