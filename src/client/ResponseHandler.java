package client;

import server.responses.PrivateChatListResponse;
import server.responses.login.LoginResponse;
import server.responses.login.LoginStatus;
import server.responses.signup.SignUpResponse;
import server.responses.signup.SignUpStatus;
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
        SignUpStatus status = response.getStatus();
        if(status == SignUpStatus.VALID) {
            System.out.println("User created successfully!");
            return response.getUser();
        }
        else if(status == SignUpStatus.INVALID)
            return null;
        else
            return null;
    }
    public int privateChatListResponse(PrivateChatListResponse response) {

    }
}
