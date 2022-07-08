package shared.requests;

public class PinRequest extends Request{
    private final String time;
    private final String[] placeholder;
    //constructor
    public PinRequest(String time, String... placeholder) {
        super(RequestType.PIN_MESSAGE);
        this.time = time;
        this.placeholder = placeholder;
    }
    //getters
    public String getTime() {
        return time;
    }
    public String[] getPlaceHolder() {
        return placeholder;
    }
}
