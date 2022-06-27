package shared.responses.signup;

import shared.user.User;

public class SignUpResponse {
    private final SignUpStatus status;
    private final User user;

    public SignUpResponse(SignUpStatus status, User user) {
        this.status = status;
        this.user = user;
    }

    public SignUpStatus getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }
}
