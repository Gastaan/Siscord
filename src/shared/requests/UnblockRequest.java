package shared.requests;

public class UnblockRequest extends Request{
    private final String username;
    //constructor
    public UnblockRequest(String username) {
        super(ReqType.UNBLOCK_USER);
        this.username = username;
    }
    //getters
    public String getUsername() {
        return username;
    }
}
