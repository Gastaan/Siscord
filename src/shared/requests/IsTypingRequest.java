package shared.requests;

public class IsTypingRequest extends Request {
    private final String username;
    //constructor
    public IsTypingRequest(String username) {
        super(ReqType.IS_TYPING);
        this.username = username;
    }
    //getters
    public String getUsername() {
        return username;
    }
}
