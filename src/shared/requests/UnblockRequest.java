package shared.requests;

public class UnblockRequest extends Request{
    private final String value;
    //constructor
    public UnblockRequest(String value) {
        super(ReqType.UNBLOCK_USER);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}
