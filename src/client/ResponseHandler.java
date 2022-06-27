package client;

import shared.responses.ChatResponse;
import shared.responses.PrivateChatListResponse;
import shared.responses.login.LoginResponse;
import shared.responses.login.LoginStatus;
import shared.responses.signup.SignUpResponse;
import shared.responses.signup.SignUpStatus;
import shared.user.User;
import shared.user.data.message.Message;

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
        for (String chatName : response.getChatNames()) {
            System.out.println(index + "-" + chatName);
            index++;
        }
        System.out.println(index + "-" + "back to the main page");
        int choice;
        do {
            System.out.println("Enter your choice: ");
            choice = scanner.nextInt();
        } while (choice < 1 || choice > index + 1);
        return choice;
    }
    public void chatResponse(ChatResponse chatResponse) {
        int index = 0;
        for(Message message : chatResponse.getMessages()) {
            index++;
            System.out.println(index + "-" + message);
        }
    }
}
