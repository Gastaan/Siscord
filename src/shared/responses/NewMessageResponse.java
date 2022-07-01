package shared.responses;

public class NewMessageResponse extends Response {
    private final boolean isDelivered;
    //constructor
    public NewMessageResponse(boolean isDelivered) {
        super(ResType.NEW_MESSAGE);
        this.isDelivered = isDelivered;
    }
    //toString
    @Override
    public String toString() {
        return "NewMessageResponse{" +
                "isDelivered=" + isDelivered +
                '}';
    }
}
