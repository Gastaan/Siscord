package shared.requests;

public class BlockRequest extends Request {
    private final String username;
    //constructor
    public BlockRequest(String username) {
        super(ReqType.BLOCK_USER);
        this.username = username;
    }
    //getters
    public String getUsername() {
        return username;
    }
}

