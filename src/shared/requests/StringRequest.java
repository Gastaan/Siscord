package shared.requests;

public class StringRequest extends Request{
    private final String value;
    //constructor
    public StringRequest(String value, RequestType type) {
        super(type);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}
