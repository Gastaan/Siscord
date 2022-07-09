package shared.responses;

public class BooleanResponse extends Response{
    private final boolean success;
    //constructor
    public BooleanResponse(ResponseType type ,boolean success) {
        super(type);
        this.success = success;
    }
    //toString
    @Override
    public String toString() {
        return getResType() + "{" +
                "success=" + success +
                '}';
    }
}
