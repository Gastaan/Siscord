package shared.requests;

import shared.user.data.message.Reacts;

public class ReactRequest extends Request{
    private final String chatName;
    private final String time;
    private final Reacts react;
    //constructor
    public ReactRequest(String chatName, String time, Reacts react) {
        super(ReqType.PRIVATE_CHAT_REACT);
        this.chatName = chatName;
        this.time = time;
        this.react = react;
    }
    //getters
    public String getChatName() {
        return chatName;
    }
    public String getTime() {
        return time;
    }
    public Reacts getReact() {
        return react;
    }
}
