package shared.requests;

public class BlockRequest extends Request {
    private final String value;
    //constructor
    public BlockRequest(String value) {
        super(ReqType.BLOCK_USER);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}

