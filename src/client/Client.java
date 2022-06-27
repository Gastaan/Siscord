package client;

import client.requests.LoginRequest;
import client.requests.SignUpRequest;
import server.responses.login.LoginResponse;
import server.responses.signup.SignUpResponse;
import user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
    private void privateChats() {

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
