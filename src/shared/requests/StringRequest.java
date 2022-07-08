package shared.requests;

public class StringRequest extends Request{
    private final String value;
    //constructor
    public StringRequest(String value, ReqType type) {
        super(type);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}
