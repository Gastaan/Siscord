package shared.responses;

import shared.responses.ResponseType;
import shared.responses.Response;
import shared.responses.login.LoginStatus;
import shared.user.User;

public class LoginResponse extends Response {
    private final User user;
    private final boolean success;
    public LoginResponse(boolean success, User user) {
        super(ResponseType.LOGIN);
        this.user = user;
        this.success = success;
    }
    public User getUser() {
        return user;
    }
    public LoginStatus getStatus() {
        return success;
    }
}
