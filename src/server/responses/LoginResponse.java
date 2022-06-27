package server.responses;

import user.User;

public class LoginResponse extends Response{
    private final User user;
    private final LoginStatus status;
    public LoginResponse(LoginStatus status, User user) {
        super(ResType.LOGIN);
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
