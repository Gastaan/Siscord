package shared.requests;

public class ChatRequest extends Request {
    private final String[] placeholder;

    public ChatRequest(String... placeholder) {
        super(ReqType.CHAT_REQUEST);
        this.placeholder = placeholder;
    }

    public String[] getPlaceholder() {
        return placeholder;
    }
}
