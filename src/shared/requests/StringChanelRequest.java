package shared.requests;

public class StringChanelRequest extends Request{
    private final String value;
    private final String[] placeholder;
    //constructor
    public StringChanelRequest(RequestType type, String value, String... placeholder) {
        super(type);
        this.value = value;
        this.placeholder = placeholder;
    }
    //getters
    public String getValue() {
        return value;
    }
    public String[] getPlaceHolder() {
        return placeholder;
    }
}
