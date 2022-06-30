//Ya Ali
package client;

import shared.requests.*;
import shared.responses.*;
import shared.responses.login.LoginResponse;
import shared.responses.signup.SignUpResponse;
import shared.user.User;
import shared.user.data.message.FileMessage;
import shared.user.data.message.Message;
import shared.user.data.message.TextMessage;

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
    private PrivateChatListResponse chatList; //TODO : should be alive while user is online
    private ChatResponse chat; //TODO: should be alive while user is online
    private GetFriendsListResponse friends;
    private IncomingFriendRequestsResponse friendRequests;
    private GetOutgoingFriendResponse outgoingFriendRequests;
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
    private synchronized void handleResponse(Response response) {
        if(response.getResType() == ResType.SIGNUP) {
            user = responseHandler.signUpResponse((SignUpResponse) response);
            notify();
        }
        else if(response.getResType() == ResType.LOGIN) {
            user = responseHandler.loginResponse((LoginResponse) response);
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
        else if(response.getResType() == ResType.NOTIFICATION) {
            System.out.println(((Notification) response).getDescription());
            System.out.flush();
        }
        else if(response.getResType() == ResType.NEW_MESSAGE) {
            Message newMessage = responseHandler.newMessageResponse((NewMessageResponse) response);
            chat.addMessage(newMessage);
            System.out.println(chat.getMessages().size() + "-" + newMessage);
        }
        else if(response.getResType() == ResType.INCOMING_FRIEND_REQUESTS) {
            friendRequests = (IncomingFriendRequestsResponse) response;
            int index = 1;
            for(String request : friendRequests.getIncomingFriendRequests()) {
                System.out.println(index + "-" + request);
                index++;
            }
            notify();
        }
        else if(response.getResType() == ResType.ADD_FRIEND) {
            System.out.println(response);
            notify();
        }
        else if(response.getResType() == ResType.GET_FRIENDS_LIST) {
            int index = 1;
            for(String friend : friends.getFriends()) {
                System.out.println(index + "-" + friend);
                index++;
            }
            notify();
        }
        else if(response.getResType() == ResType.GET_OUTGOING_FRIEND) {
            for(String outgoingFriend : outgoingFriendRequests.getOutgoingFriendRequests()) {
                System.out.println(outgoingFriend);
            }
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
    private synchronized void login() {
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
    private synchronized void signUp() throws IOException, ClassNotFoundException {
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
        clearConsole();
        int choice;
        do {
            System.out.println("1- private chats\n2- servers\n3- new private chat\n4- friends\n5- add friend\n6-incoming friend requests\n7-pending requests\n8-blocked users\n9- setting\n910- exit");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> privateChats();
                case 2 -> servers();
                case 3 -> newPrivateChat();
                case 4 -> friends();
                case 5 -> addFriend();
                case 6 -> incomingFriendRequests();
                case 7 -> outGoingFriendRequests();
                case 8 -> blockedUsers();
                case 9 -> setting();
                case 10 -> System.out.println("Bye Bye!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice != 10);
        user = null;
    }
    private synchronized void outGoingFriendRequests() {
        int choice;
        try {
            request.writeObject(new Request(ReqType.GET_OUTGOING_FRIEND));
            wait();
            do {
                System.out.println("1-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> System.out.println("Ok!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 1);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private synchronized void blockedUsers() {} //TODO : implement
    private synchronized void incomingFriendRequests() {
        int choice;
        try {
            request.writeObject(new GetFriendRequestsRequest(user.getUsername()));
            wait();
            do {
                System.out.println("1-select request\n2-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> selectRequest();
                    case 2 -> System.out.println("Bye Bye!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 2);
        }
        catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void selectRequest() {
        int requestID, choice;
        Boolean accepted = null;
        do {
            System.out.println("Enter request id: ");
            requestID = scanner.nextInt();
        }
        while (requestID >  friendRequests.getIncomingFriendRequests().size() || requestID < 1);
        do {
            System.out.println("1-accept\n2-decline\n3-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> accepted = true;
                case 2 -> accepted = false;
                case 3 -> System.out.println("Bye Bye!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice > 3 || choice < 1);
        if(accepted != null) {
            try {
                request.writeObject(new FriendRequestAnswerRequest(user.getUsername(),friendRequests.getIncomingFriendRequests().get(requestID - 1), accepted));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void chatPage(int chosenChat) {
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
        int choice;
        do {
            System.out.println("1-text\n2-file\n3-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter your message: ");
                    scanner.nextLine();
                    String message = scanner.nextLine();
                    Message newMessage = new TextMessage(message, user.getUsername());
                    try {
                        request.writeObject(new NewPrivateChatMessageRequest(newMessage, user.getUsername(), chatsName));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> sendFileMessages();
            }
        } while (choice != 3);
    }
    private void sendFileMessages() {} //TODO : send file messages
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
        /*
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

         */
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
    private void addFriend() {
        int choice;
        do {
            System.out.println("1-Enter username\n2-Back to the main page");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter username: ");
                    String username = scanner.next();
                    try {
                        request.writeObject(new AddFriendRequest(user.getUsername(), username));
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
    private void friends()  {
        int choice;
        do {
            try {
                request.writeObject(new GetFriendsListRequest(user.getUsername()));
                wait();
            }
             catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("1-select friend\n2-Back to the main page");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> selectFriend();
                case 2 -> System.out.println("Ok");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice != 2);
    }
    private void selectFriend() {
        int requestID, choice;
        do {
            System.out.println("Enter request id: ");
            requestID = scanner.nextInt();
        }
        while (requestID >  friends.getFriends().size() || requestID < 1);
        do {
            System.out.println("1-remove\n2-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    try {
                        request.writeObject(new RemoveFriendRequest(user.getUsername(), friends.getFriends().get(requestID - 1)));
                        System.out.println("Ok!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case 2 -> System.out.println("Bye Bye!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice > 2 || choice < 1);
    }
    private void setting() {

    } //Last : TODO : SETTING
    public  static void clearConsole()
    {
        for (int i = 0; i < 50; ++i) System.out.println();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
