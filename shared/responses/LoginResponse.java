package shared.responses;

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
    public boolean getStatus() {
        return success;
    }
}
