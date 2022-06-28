package client;

import shared.requests.*;
import shared.responses.ChatResponse;
import shared.responses.PrivateChatListResponse;
import shared.responses.ResType;
import shared.responses.Response;
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
    private Thread listener;
    private void listener() {
        listener = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(serverConnection.isConnected()) {
                        handleResponse((Response) response.readObject());
                    }
                }
                catch(IOException | ClassNotFoundException e) {
                    System.err.println("Problem occurred while listening!");
                }
            }
        });
        listener.start();
    }
    private void handleResponse(Response response) {
        if(response.getResType() == ResType.SIGNUP) {
            user = responseHandler.loginResponse((LoginResponse) response);
            notify();
        }
        else if(response.getResType() == ResType.LOGIN) {
            user = responseHandler.signUpResponse((SignUpResponse) response);
            notify();
        }
        else if(response.getResType() == ResType.PRIVATE_CHAT_LIST) {

        }
        else if(response.getResType() == ResType.PRIVATE_CHAT_LIST) {

        }
        else if(response.getResType() == ResType.PRIVATE_CHAT) {

        }

    }
    public Client() {
        try {
            serverConnection = new Socket("localhost", 404); //TODO: should be changed to 3112
             request = new ObjectOutputStream(serverConnection.getOutputStream());
             response = new ObjectInputStream(serverConnection.getInputStream());
             responseHandler = new ResponseHandler();
            listener();
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
            wait();
                if (user != null)
                    homePage();
        } catch (IOException | InterruptedException e) {
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
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (user == null);
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
            System.out.println("1-sendMessage\n2-React\n3-back to the main page\n4-make voice call");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> sendMessage(messages, chatsName);
                case 2 -> react(messages, chatsName);
                case 3 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice < 1 || choice > 3);
    }
    private void sendMessage(ArrayList<Message> messages, String chatsName) {
        try {
            request.writeObject(new IsTypingRequest(user, chatsName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
    private void privateChats() { //should be alive TODO
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
        int choice;
        do {
            System.out.println("1-Enter username\n2-Back to the main page");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter username: ");
                    String username = scanner.next();
                    try {
                        request.writeObject(new NewPrivateChatRequest(user.getUsername(), username));
                        wait();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> System.out.println("Ok");
                default -> System.out.println("Invalid Choice!");
            }
        } while(choice != 2);
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
