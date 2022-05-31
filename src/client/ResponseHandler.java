package client;

import server.responses.Response;

public class ResponseHandler {
    public User loginResponse(Response responded) {
        if(responded.getDescription().equals("passed")) {
            return (User) responded.getResponded();
        }
        else {
            System.out.println("Wrong username or password!");
            return null;
        }
    }
    public Boolean signUpResponse(Response responded) {

    }
}
