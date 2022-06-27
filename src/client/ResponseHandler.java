package client;

import server.responses.login.LoginResponse;
import server.responses.login.LoginStatus;
import server.responses.signup.SignUpResponse;
import user.User;

public class ResponseHandler {
    public User loginResponse(LoginResponse loginResponse) {
        if (loginResponse.getStatus() == LoginStatus.SUCCESS) {
            return loginResponse.getUser();
        } else {
            return null;
        }
    }
    public User signUpResponse(SignUpResponse response) {

    }
}
