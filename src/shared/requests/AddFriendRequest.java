package shared.requests;

public class AddFriendRequest extends Request {
    private final String value;
    //constructor
    public AddFriendRequest( String value) {
        super(ReqType.ADD_FRIEND);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}
