package shared.requests;

public class ChatRequest extends Request{
    private final String username;

    public ChatRequest(String username) {
        super(ReqType.PRIVATE_CHAT);
        this.username = username;
    }


public String getUsername() {
        return username;
    }
}
