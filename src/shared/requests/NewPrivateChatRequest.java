package shared.requests;

public class NewPrivateChatRequest extends Request {
    private final String value;

    public NewPrivateChatRequest(String value) {
        super(ReqType.NEW_PRIVATE_CHAT);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}

