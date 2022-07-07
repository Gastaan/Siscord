package shared.responses;

public class PinResponse extends Response{
    private final boolean success;
    //constructor
    public PinResponse(boolean success) {
        super(ResType.PIN_MESSAGE);
        this.success = success;
    }
    //toString
    @Override
    public String toString() {
        return "PinResponse{" +
                "status=" + success +
                '}';
    }
}
