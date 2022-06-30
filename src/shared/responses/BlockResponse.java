package shared.responses;

public class BlockResponse extends Response{
    private final boolean success;
    //constructor
    public BlockResponse(boolean success) {
        super(ResType.BLOCK_USER);
        this.success = success;
    }
    //toString
    @Override
    public String toString() {
        return "BlockResponse{" +
                "status=" + success +
                '}';
    }
}
