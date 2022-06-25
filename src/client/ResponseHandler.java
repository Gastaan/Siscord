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
        String description = responded.getDescription();
        if(description.equals("valid")) {
            System.out.println("Signed up successfully!");
            return true;
        }
        else {
            if(description.equals("username exists"))
                System.out.println("Username exists!");
            else
                System.out.println("Invalid!");
            return false;
        }
    }
}
