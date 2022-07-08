package shared.requests;

public class NewServerRequest extends Request{
    private final String value;
    //constructor
    public NewServerRequest(String value) {
        super(ReqType.NEW_SERVER);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}
