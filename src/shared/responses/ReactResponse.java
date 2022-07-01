package shared.responses;

public class ReactResponse extends Response{
    private final boolean success;
    //constructor
    public ReactResponse(boolean success) {
        super(ResType.PRIVATE_CHAT_REACT);
        this.success = success;
    }
    //toString
    @Override
    public String toString() {
        return "ReactResponse{" +
                "success=" + success +
                '}';
    }
}
