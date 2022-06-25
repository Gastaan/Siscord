package client;

import client.requests.ReqType;
import client.requests.Request;
import server.responses.Response;

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
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
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
            request.writeObject(new Request(ReqType.LOGIN,   username + " " + password));
            try {
                Response responded = (Response) response.readObject();
                user = responseHandler.loginResponse(responded);
                if (user != null)
                    user.homePage();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private  void signUp() throws IOException, ClassNotFoundException {
        String username, password, mail = "";
        int choice;
        do {
            System.out.println("Enter username: ");
                username = scanner.next();
            System.out.println("Enter password: ");
                password = scanner.next();
            do {
                System.out.println("Do you want to enter your password ?\n1-Yes\n2-No");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> mail = scanner.next();
                    case 2 -> {
                        System.out.println("Ok");
                        break;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } while (choice < 1 || choice > 2);
            request.writeObject(new Request(ReqType.LOGIN,   username + " " + password + " "  + mail));
        } while (!responseHandler.signUpResponse((Response) response.readObject()));
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

}
