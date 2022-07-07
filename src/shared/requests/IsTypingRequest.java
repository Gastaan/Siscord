package shared.requests;

public class IsTypingRequest extends Request {
    private final String[] placeholder;
    //constructor
    public IsTypingRequest(String... placeholder) {
        super(ReqType.IS_TYPING);
        this.placeholder = placeholder;
    }
    //getters
    public String[] getPlaceholder() {
        return placeholder;
    }
}
