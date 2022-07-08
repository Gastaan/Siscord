package shared.requests;

public class ChangePasswordRequest extends Request{
    private final String value;
    //constructor
    public ChangePasswordRequest(String value) {
        super(ReqType.CHANGE_PASSWORD);
        this.value = value;
    }
    //getters
    public String getValue() {
        return value;
    }
}
