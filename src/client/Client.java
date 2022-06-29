package client;

import shared.requests.*;
import shared.responses.*;
import shared.responses.login.LoginResponse;
import shared.responses.signup.SignUpResponse;
import shared.user.User;
import shared.user.data.message.FileMessage;
import shared.user.data.message.Message;
import shared.user.data.message.Reacts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    //fields
    private static final Scanner scanner = new Scanner(System.in);
    private  Socket serverConnection;
    private User user;
    private ObjectInputStream response;
    private ObjectOutputStream request;
    private ResponseHandler responseHandler;
    private PrivateChatListResponse chatList;
    private ChatResponse chat;
    //constructor
    public Client() {
        try {
            serverConnection = new Socket("localhost", 3132);
            request = new ObjectOutputStream(serverConnection.getOutputStream());
            response = new ObjectInputStream(serverConnection.getInputStream());
            responseHandler = new ResponseHandler();
            listener();
        } catch (IOException e) {
            System.out.println("Can not connect to server!");
            System.exit(404);
        }
    }
    //methods
    private void listener() {
            new  Thread(new Runnable() {
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
        }).start();
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
            chatList = (PrivateChatListResponse) response;
            notify();
        }
        else if(response.getResType() == ResType.PRIVATE_CHAT) {
            ChatResponse chat = (ChatResponse)response;
            downLoadFileMessages();
            responseHandler.chatResponse(chat);
            notify();
        }
        else if(response.getResType() == ResType.NOTIFICATION)
            System.out.println(((Notification) response).getDescription());
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
    private void chatPage(int chosenChat) {
        ArrayList<Message> messages = new ArrayList<>(chat.getMessages());
        String chatsName = chatList.getChatNames().get(chosenChat - 1);
        int choice;
        do {
            System.out.println("1-sendMessage\n2-React\n3-make voice call\n4-back to home page");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> sendMessage(chatsName);
                case 2 -> react(chatsName);
                case 3 -> voiceCall();
                case 4 -> System.out.println("OK!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice < 1 || choice > 3);
    }
    private void sendMessage(String chatsName) {
        try {
            request.writeObject(new IsTypingRequest(user, chatsName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void downLoadFileMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(Message message : chat.getMessages()) {
                    if(message instanceof FileMessage) {
                        ((FileMessage) message).download();
                    }
                }
            }
        }).start();
    }
    private void voiceCall() {

    } //TODO : VOICE_CALL
    private void react(String chatsName) {
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
    } //TODO : REACT
    private void privateChats() {
        System.out.println("Private chats: ");
        int choice;
        try {
            request.writeObject(new PrivateChatListRequest(user.getUsername()));
            wait();
            choice = responseHandler.privateChatListResponse(chatList);
            if(choice != chatList.getCount() + 1) {
                request.writeObject(new ChatRequest(user.getUsername() , chatList.getChatNames().get(choice - 1)));
                wait();
                ArrayList<Message> messages = new ArrayList<>(chat.getMessages());
                chatPage(choice);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void servers() {

    }//TODO : SERVERS
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
    }//TODO : NEW_PRIVATE_CHAT
    private void friendsStatus() {

    }//TODO : FRIENDS_STATUS
    private void addFriend() {

    } //TODO : ADD_FRIEND
    private void removeFriend() {

    } //TODO : REMOVE_FRIEND
    private void setting() {

    } //TODO : SETTING
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
