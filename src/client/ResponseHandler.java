package client;

import server.responses.PrivateChatListResponse;
import server.responses.login.LoginResponse;
import server.responses.login.LoginStatus;
import server.responses.signup.SignUpResponse;
import server.responses.signup.SignUpStatus;
import user.User;

import java.util.Scanner;

public class ResponseHandler {
    private static final Scanner scanner = new Scanner(System.in);
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
        else if(status == SignUpStatus.INVALID) {
            System.out.println("Invalid inputs provided!");
            return null;
        }
        else {
            System.out.println("User already exists!");
            return null;
        }
    }
    public int privateChatListResponse(PrivateChatListResponse response) {
        int index = 1;
        for(String chatName : response.getChatNames()) {
            System.out.println(index + "-" + chatName);
            index++;
        }
        System.out.println(index + "-" + "back to the main page");
        int choice;
        do {
            System.out.println("Enter your choice: ");
            choice = scanner.nextInt();
        } while(choice < 1 || choice > index + 1);
        return choice;
    }
}
