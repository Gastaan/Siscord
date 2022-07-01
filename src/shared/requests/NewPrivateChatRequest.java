package shared.requests;

public class NewPrivateChatRequest extends Request {
    private final String username;

    public NewPrivateChatRequest(String username) {
        super(ReqType.NEW_PRIVATE_CHAT);
        this.username = username;
    }
    //getters
    public String getUsername() {
        return username;
    }
}

