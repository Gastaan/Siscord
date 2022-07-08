package shared.responses.login;

import shared.responses.ResponseType;
import shared.responses.Response;
import shared.user.User;

public class LoginResponse extends Response {
    private final User user;
    private final LoginStatus status;
    public LoginResponse(LoginStatus status, User user) {
        super(ResponseType.LOGIN);
        this.user = user;
        this.status = status;
    }
    public User getUser() {
        return user;
    }
    public LoginStatus getStatus() {
        return status;
    }
}
