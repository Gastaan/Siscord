package shared.requests;

import shared.user.data.message.Reacts;

public class ReactRequest extends Request{
    private final String[] placeholder;
    private final String time;
    private final Reacts react;
    //constructor
    public ReactRequest(String time, Reacts react, String... placeholder) {
        super(ReqType.CHAT_REACT);
        this.placeholder = placeholder;
        this.time = time;
        this.react = react;
    }
    //getters
    public String[] getPlaceholder() {
        return placeholder;
    }
    public String getTime() {
        return time;
    }
    public Reacts getReaction() {
        return react;
    }
}
