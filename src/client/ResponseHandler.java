package client;

import server.responses.LoginResponse;
import server.responses.LoginStatus;
import server.responses.SignUpResponse;
import user.User;
import server.responses.Response;

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
