package shared.requests;

public class ChangePasswordRequest extends Request{
    private final String newPassword;
    //constructor
    public ChangePasswordRequest(String newPassword) {
        super(ReqType.CHANGE_PASSWORD);
        this.newPassword = newPassword;
    }
    //getters
    public String getNewPassword() {
        return newPassword;
    }
}
