package client;

import shared.requests.*;
import shared.responses.ChatResponse;
import shared.responses.PrivateChatListResponse;
import shared.responses.login.LoginResponse;
import shared.responses.signup.SignUpResponse;
import shared.user.User;
import shared.user.data.message.Message;
import shared.user.data.message.Reacts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private  Socket serverConnection;
    private User user;
    private ObjectInputStream response;
    private ObjectOutputStream request;
    private ResponseHandler responseHandler;
    public Client() {
        try {
            serverConnection = new Socket("localhost", 404);
             request = new ObjectOutputStream(serverConnection.getOutputStream());
             response = new ObjectInputStream(serverConnection.getInputStream());
            responseHandler = new ResponseHandler();
             //getResponse();
        } catch (IOException e) {
            System.out.println("Can not connect to server!");
            System.exit(404);
        }
    }
    public void start() {
        int choice;
        do {
            System.out.println("1-login\n2-sign up\n3-exit");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> login();
                case 2 -> {
                    try {
                        signUp();
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 3 -> System.out.println("Bye Bye!");
                default -> System.out.println("Invalid Choice!");
            }
        } while(choice != 3);
    }
    private void login() {
        System.out.println("Enter user username: ");
        String username = scanner.next();
        System.out.println("Enter your password: ");
        String password = scanner.next();
        try {
            request.writeObject(new LoginRequest(username, password));
            try {
                LoginResponse responded = (LoginResponse) response.readObject();
                user = responseHandler.loginResponse(responded);
                if (user != null)
                    homePage();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private  void signUp() throws IOException, ClassNotFoundException {
        String username, password, mail, phoneNumber = "";
        int choice;
        A :  do {
            System.out.println("Back to the main page ?\n1-yes\n2-no");
            do {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 :
                        break A;
                    case 2 :
                        System.out.println("Ok");
                        break;
                    default:
                        System.out.println("Invalid Option!");
                }
            } while (choice > 2 || choice < 1);
            System.out.println("Enter username: ");
                username = scanner.next();
            System.out.println("Enter password: ");
                password = scanner.next();
            System.out.println("Enter mail: ");
                mail = scanner.next();
            do {
                System.out.println("Do you want to enter your phoneNumber ?\n1-Yes\n2-No");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> phoneNumber = scanner.next();
                    case 2 -> System.out.println("Ok");
                    default -> System.out.println("Invalid choice!");
                }
            } while (choice < 1 || choice > 2);
            request.writeObject(new SignUpRequest(username, password, mail, phoneNumber));
        } while ((user = responseHandler.signUpResponse((SignUpResponse) response.readObject())) == null);
        if(user != null)
            homePage();
    }
    private void homePage() {
        int choice;
        do {
            System.out.println("1- private chats\n2- servers\n3- new private chat\n4- friends status\n5- add friend\n6- remove friend\n7- setting\n8- exit");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> privateChats();
                case 2 -> servers();
                case 3 -> newPrivateChat();
                case 4 -> friendsStatus();
                case 5 -> addFriend();
                case 6 -> removeFriend();
                case 7 -> setting();
                case 8 -> System.out.println("Bye Bye!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice != 8);
        user = null;
    }
    private void chatPage(ArrayList<Message> messages, String chatsName) {
        int choice;
        do {
            System.out.println("1-sendMessage\n2-React\n3-back to the main page");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> sendMessage();
                case 2 -> react(messages, chatsName);
                case 3 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice < 1 || choice > 3);
    }
    private void sendMessage() {

    }
    private void react(ArrayList<Message> messages, String chatsName) {
        int choice, size = messages.size();
        do {
                System.out.println("Which message ?" + 1 + "-" + size);
                choice = scanner.nextInt();
                if(choice > size || choice < 1)
                    System.out.println("Invalid Choice!");
            }
         while (choice < 1 || choice > size);
        do {
            System.out.println("1-like\n2-dislike\n3-lol\n4-back to the main page");
            choice = scanner.nextInt();
        }while (choice < 1 || choice > 4);
        Reacts react;
        switch (choice) {
            case 1 -> react = Reacts.LIKE;
            case 2 -> react = Reacts.DISLIKE;
            case 3 -> react = Reacts.LOL;
            default -> {
                System.out.println("Ok!");
                return;
            }
        }
        try {
            request.writeObject(new ReactRequest(user, chatsName, messages.get(choice - 1), react));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void privateChats() {
        System.out.println("Private chats: ");
        int choice;
        try {
            request.writeObject(new PrivateChatListRequest(user.getUsername()));
            PrivateChatListResponse chatList = (PrivateChatListResponse) response.readObject();
            choice = responseHandler.privateChatListResponse(chatList);
            if(choice != chatList.getCount() + 1) {
                request.writeObject(new ChatRequest (user.getUsername() , chatList.getChatNames().get(choice - 1)));
                ChatResponse chat = (ChatResponse) response.readObject();
                responseHandler.chatResponse(chat);
                chatPage(chat.getMessages() , chatList.getChatNames().get(choice - 1));
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void servers() {

    }
    private void newPrivateChat() {

    }
    private void friendsStatus() {

    }
    private void addFriend() {

    }
    private void removeFriend() {

    }
    private void setting() {

    }
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
