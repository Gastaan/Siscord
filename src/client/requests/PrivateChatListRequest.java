package client.requests;

public class PrivateChatListRequest extends Request {
    private final String username;
    public PrivateChatListRequest(String username) {
        super(ReqType.PRIVATE_CHAT_LIST);
        this.username = username;
    }
    //getter
    public String getUsername() {
        return username;
    }
}
