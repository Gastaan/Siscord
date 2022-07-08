package shared.requests;

public class RemoveFriendRequest extends Request{
    private final String value;
    //constructor
    public RemoveFriendRequest(String value) {
        super(ReqType.REMOVE_FRIEND);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}
