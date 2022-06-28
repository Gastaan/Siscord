package shared.responses.signup;

import shared.responses.ResType;
import shared.responses.Response;
import shared.user.User;

public class SignUpResponse extends Response {
    private final SignUpStatus status;
    private final User user;

public SignUpResponse(SignUpStatus status, User user) {
        super(ResType.SIGNUP);
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
