package shared.requests;

public class ChatRequest extends Request{
    private final String requestedUser;
    private final String username;

    public ChatRequest(String requestedUser, String username) {
        super(ReqType.PRIVATE_CHAT);
        this.requestedUser = requestedUser;
        this.username = username;
    }

    public String getRequestedUser() {
        return requestedUser;
    }

public String getUsername() {
        return username;
    }
}
