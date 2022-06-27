package client.requests;

public class ChatRequest extends Request{
    private final String username;

public ChatRequest(String username) {
        super(ReqType.CHAT_REQUEST);
        this.username = username;
    }
    //getters
    public String getUsername() {
        return username;
    }
}
