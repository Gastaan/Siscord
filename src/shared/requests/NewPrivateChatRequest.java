package shared.requests;

public class NewPrivateChatRequest extends Request {
    private final String user1;
    private final String user2;

    public NewPrivateChatRequest(String user1, String user2) {
        super(ReqType.NEW_PRIVATE_CHAT);
        this.user1 = user1;
        this.user2 = user2;
    }
    //getters
    public String getUser1() {
        return user1;
    }
    public String getUser2() {
        return user2;
    }
}

