package shared.responses;

public class UnblockResponse extends Response{
    private final boolean success;
    //constructor
    public UnblockResponse(boolean success) {
        super(ResponseType.UNBLOCK_USER);
        this.success = success;
    }
    //toString
    @Override
    public String toString() {
        return "UnblockResponse{" +
                "status=" + success +
                '}';
    }
}
