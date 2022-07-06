//Ya Ali
package client;

import shared.requests.*;
import shared.responses.*;
import shared.responses.list.ChatListResponse;
import shared.responses.login.LoginResponse;
import shared.responses.signup.SignUpResponse;
import shared.user.User;
import shared.user.data.message.FileMessage;
import shared.user.data.message.Message;
import shared.user.data.message.Reacts;
import shared.user.data.message.TextMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    //fields
    private static final Scanner scanner = new Scanner(System.in);
    private  Socket serverConnection;
    private User user;
    private ObjectInputStream response;
    private ObjectOutputStream request;
    private ResponseHandler responseHandler;
    private ChatListResponse chatList;
    private ChatResponse chat;
    private GetFriendsListResponse friends;
    private IncomingFriendRequestsResponse friendRequests;
    private GetOutgoingFriendResponse outgoingFriendRequests;
    private GetBlockedUsersResponse blockedUsers;
    private ServerListResponse serverList;
    private ChanelListResponse chanels;
    //colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
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
        else if(response.getResType() == ResType.LIST) {
            chatList = (ChatListResponse) response;
            int index = 1;
            for(String chatName : chatList.getChatNames()) {
                System.out.println(index++  + ": " + chatName);
            }
            notify();
        }
        else if(response.getResType() == ResType.PRIVATE_CHAT) {
            chat = (ChatResponse)response;
            responseHandler.chatResponse(chat);
            downLoadFileMessages();
            notify();
        }
        else if(response.getResType() == ResType.NOTIFICATION) {
            System.out.println(((Notification) response).getDescription());
            System.out.flush();
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
            friends = (GetFriendsListResponse) response;
            int index = 1;
            for(String friend : friends.getFriends()) {
                System.out.println(index + "-" + friend);
                index++;
            }
            notify();
        }
        else if(response.getResType() == ResType.GET_OUTGOING_FRIEND) {
            outgoingFriendRequests = (GetOutgoingFriendResponse) response;
            for(String outgoingFriend : outgoingFriendRequests.getOutgoingFriendRequests())
                System.out.println(outgoingFriend);
            notify();
        }
        else if(response.getResType() == ResType.GET_BLOCKED_USERS) {
            int index = 1;
            blockedUsers = (GetBlockedUsersResponse) response;
            for(String blockedUser : blockedUsers.getBlockedUsers())
                System.out.println(index++ + "-" +blockedUser);
            notify();
        }
        else if(response.getResType() == ResType.UNBLOCK_USER) {
            System.out.println(response);
            notify();
        }
        else if(response.getResType() == ResType.BLOCK_USER) {
            System.out.println(response);
            notify();
        }
        else if(response.getResType() == ResType.NEW_MESSAGE) {
            System.out.println(response);
            notify();
        }
        else if(response.getResType() == ResType.PRIVATE_CHAT_REACT)  {
            System.out.println(response);
            notify();
        }
        else if(response.getResType() == ResType.NEW_PRIVATE_CHAT) {
            System.out.println(response);
            notify();
        }
        else if(response.getResType() == ResType.NEW_SERVER) {
            System.out.println(response);
            notify();
        }
        else if(response.getResType() == ResType.SERVER_LIST) {
            serverList = (ServerListResponse) response;
            int index = 1;
            for(String server : serverList.getServers()) {
                System.out.println(index++ + "-" + server);
            }
            notify();
        }
        else if(response.getResType() == ResType.CHANEL_LIST) {
            chanels = (ChanelListResponse) response;
            int index = 1;
            for(String chanel : chanels.getChanelNames()) {
                System.out.println(index++ + "-" + chanel);
            }
            notify();
        }
        else if(response.getResType() == ResType.CHANGE_PASSWORD) {
            System.out.println(response);
            notify();
        }
    }
    public void start() {
        int choice;
        try {
            do {
                System.out.println(ANSI_YELLOW + "1-login\n2-sign up\n3-exit" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> login();
                    case 2 -> signUp();
                    case 3 -> System.out.println(ANSI_BLUE + "Bye Bye!" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid !" + ANSI_RESET);
                }
            } while (choice != 3);
        }
        catch (Exception e) {
            System.err.println("Problem occurred!");
            e.printStackTrace();
        }
        finally {
            close();
        }
    }
    private synchronized void login() throws IOException, InterruptedException {
            System.out.println(ANSI_WHITE + "Enter user username: " + ANSI_RESET);
            String username = scanner.next();
            System.out.println(ANSI_WHITE + "Enter your password: " + ANSI_RESET);
            String password = scanner.next();
            request.writeObject(new LoginRequest(username, password));
            wait();
                if (user != null)
                    homePage();
    }
    private synchronized void signUp() throws IOException, InterruptedException {
        String username, password, mail, phoneNumber = "";
        int choice;
        A :  do {
            do {
                System.out.println(ANSI_PURPLE + "Back to the main page ?\n1-yes\n2-no" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                }
                catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 :
                        break A;
                    case 2 :
                        System.out.println(ANSI_BLUE + "Ok" + ANSI_RESET);
                        break;
                    default:
                        System.out.println(ANSI_RED + "Invalid Option!" + ANSI_RESET);
                }
            } while (choice > 2 || choice < 1);
            System.out.println(ANSI_WHITE + "Enter username: " + ANSI_RESET);
                username = scanner.next();
            System.out.println(ANSI_WHITE + "Enter password: " + ANSI_RESET);
                password = scanner.next();
            System.out.println(ANSI_WHITE + "Enter mail: " + ANSI_RESET);
                mail = scanner.next();
            do {
                System.out.println(ANSI_PURPLE + "Do you want to enter your phoneNumber ?\n1-Yes\n2-No" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                }
                catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> phoneNumber = scanner.next();
                    case 2 -> System.out.println("Ok");
                    default -> System.out.println("Invalid choice!");
                }
            } while (choice < 1 || choice > 2);
                request.writeObject(new SignUpRequest(username, password, mail, phoneNumber));
                wait();
        } while (user == null);
        if(user != null)
            homePage();
    }
    private void homePage() {
        clearConsole();
        int choice;
        do {
            System.out.println("1- private chats\n2- servers\n3- friends\n4- add friend\n5-incoming friend requests\n6-pending requests\n7-blocked users\n8- setting\n9- exit");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> privateChats();
                case 2 -> servers();
                case 3 -> friends();
                case 4 -> addFriend();
                case 5 -> incomingFriendRequests();
                case 6 -> outGoingFriendRequests();
                case 7 -> blockedUsers();
                case 8 -> setting();
                case 9 -> System.out.println("Bye Bye!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice != 9);
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
                if (choice == 1)
                    System.out.println("Ok!");
                else
                    System.out.println("Invalid Choice!");
            } while (choice != 1);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    } //TODO : cancel friend request
    //Blocked users can not send message or see profile photo
    private synchronized void blockedUsers() {
        int choice;
        try {
            do {
                request.writeObject(new Request(ReqType.GET_BLOCKED_USERS));
                wait();
                System.out.println("1-select\n2-block a user\n3-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> selectBlockedUser();
                    case 2 -> blockUser();
                    case 3 -> System.out.println("Ok!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 3);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private synchronized void selectBlockedUser() {
        int requestID, choice;
        do {
            System.out.println("Enter blocked id: ");
            requestID = scanner.nextInt();
        }
        while (requestID >  blockedUsers.getBlockedUsers().size()|| requestID < 1);
        do {
            System.out.println("1-unblock\n2-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 ->  {
                    try {
                        request.writeObject(new UnblockRequest( blockedUsers.getBlockedUsers().get(requestID - 1)));
                        wait();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice > 2 || choice < 1);
    }
    private void blockUser() {
        int choice;
        do {
            System.out.println("1-enter username\n2-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter username: ");
                    String username = scanner.next();
                    try {
                        request.writeObject(new BlockRequest(username));
                        wait();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice > 2 || choice < 1);
    }
    private synchronized void incomingFriendRequests() {
        int choice;
        try {
            do {
                request.writeObject(new GetFriendRequestsRequest(user.getUsername()));
                wait();
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
    private synchronized void privateChatPage(int chatIndex) {
        int choice;
        try {
            do {
                request.writeObject(new ChatRequest(chatList.getChatNames().get(chatIndex - 1)));
                wait();
                System.out.println("1-sendMessage\n2-React\n3-voice call\n4-back to home page");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> sendMessage();
                    case 2 -> react();
                    case 3 -> voiceCall();
                    case 4 -> System.out.println("OK!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 4);
        }
          catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
    }
    private void textChanelPage(int chatIndex) {
        int choice;
        try {
            do {
                request.writeObject(new ChatRequest("Server: " + chanels.getServerID() + "Chanel: " + chanels.getChanelNames().get(chatIndex - 1)));
                wait();
                System.out.println("1-sendMessage\n2-React\n3-back to home page");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> sendMessage();
                    case 2 -> react();
                    case 3 -> System.out.println("OK!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 3);
        }
        catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    private synchronized void sendMessage() {
        int choice;
        do {
            System.out.println("1-text\n2-file\n3-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    try {
                        request.writeObject(new IsTypingRequest(chat.getUsername()) );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Enter your message: ");
                    scanner.nextLine();
                    String message = scanner.nextLine();
                    Message newMessage = new TextMessage(message, user.getUsername());
                    try {
                        request.writeObject(new NewPrivateChatMessageRequest(newMessage, chat.getUsername()));
                        wait();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> sendFileMessages();
            }
        } while (choice < 1 || choice > 3);
    }
    private void uploadFileMessage(String finalFilePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileMessage newMessage = new FileMessage(user.getUsername(), Files.readAllBytes(Paths.get(finalFilePath)), finalFilePath);
                    synchronized (request) {
                        request.writeObject(new NewPrivateChatMessageRequest(newMessage, chat.getUsername()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    private void sendFileMessages() {
        String filePath;
        do {
            System.out.println("Enter file path: ");
            filePath = scanner.next();
            if(!Files.exists(Paths.get(filePath)))
                System.out.println("File not found!");
        } while (!Files.exists(Paths.get(filePath)));
        uploadFileMessage(filePath);
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
    private void react() {
        int choice, reactChoice;
        do {
                System.out.println("1-select message\n2-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 ->  {
                        int messageID;
                        do {
                            System.out.println("Enter message id: ");
                            messageID = scanner.nextInt();
                        }
                        while (messageID > chat.getMessages().size() || messageID < 1);
                        Message message = chat.getMessages().get(messageID - 1);
                        Reacts reaction = null;
                        do {
                            System.out.println("Enter your reaction: \n1-like\n2-dislike\n3-lol");
                            reactChoice = scanner.nextInt();
                            switch (reactChoice) {
                                case 1 -> reaction = Reacts.LIKE;
                                case 2 -> reaction = Reacts.DISLIKE;
                                case 3 -> reaction = Reacts.LOL;
                                default -> System.out.println("Invalid Choice!");
                            }
                        }
                        while (reactChoice > 3 || reactChoice < 1);
                            try {
                                request.writeObject(new ReactRequest(chat.getUsername(), message.getTime(), reaction));
                                wait();
                            } catch (IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                    }
                    case 2 -> System.out.println("OK!");
                    default -> System.out.println("Invalid Choice!");
                }
        } while(choice != 2);
    }
    private synchronized void privateChats() {
        int choice;
        try {
            do {
                request.writeObject(new Request(ReqType.PRIVATE_CHAT_LIST));
                wait();
                System.out.println("1-select chat\n2-new chat\n3-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> selectChat();
                    case 2 -> newPrivateChat();
                    case 3 -> System.out.println("Ok!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 3);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void selectChat() {
        int choice, chatIndex;
        do {
            System.out.println("1-select from list\n2-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    do {
                        System.out.println("Enter chat index: ");
                         chatIndex = scanner.nextInt();
                    } while (chatIndex > chatList.getChatNames().size() || chatIndex < 1);
                        privateChatPage(chatIndex);
                }
                case 2 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice > 2 || choice < 1);
    }
    private void voiceChanel(int chatIndex) {} //TODO : play music
    private void servers() {
        int choice;
        do {
            System.out.println("1-select server\n2-new server\n3-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> selectServerPage();
                case 2 -> newServer();
                case 3 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice != 3);
    }
    private void serverPage(int serverIndex) {
        int choice;
        try {
            do {
                request.writeObject(new GetChanelsRequest(serverList.getID(serverList.getServers().get(serverIndex - 1))));
                wait();
                System.out.println("1-select chanel\n2-add friend\n3-members\n4-setting\n5-leave server\n6-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> selectChanel(serverList.getID(serverList.getServers().get(serverIndex - 1)));
                 //   case 2 -> addFriend(serverList.getID(serverList.getServers().get(serverIndex - 1)));
                 //    case 3 -> members(serverList.getID(serverList.getServers().get(serverIndex - 1)));
                 //    case 4 -> serverSetting(serverList.getID(serverList.getServers().get(serverIndex - 1)));
                 //    case 5 -> leaveServer(serverList.getID(serverList.getServers().get(serverIndex - 1)));
                    case 6 -> System.out.println("Ok!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 2);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private synchronized void selectChanel(int serverID) {
        int choice;
        try {
            do {
                request.writeObject(new GetChanelsRequest(serverID));
                wait();
                System.out.println("1-select chanel\n2-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> {
                        int chanelIndex;
                        do {
                            System.out.println("Enter chanel index: ");
                            chanelIndex = scanner.nextInt();
                        } while (chanelIndex > chanels.getChanelNames().size() || chanelIndex < 1);
                        if(chanels.getChanelNames().get(chanelIndex - 1).contains("Voice Channel"))
                            voiceChanel(serverID);
                        else
                            textChanelPage(chanelIndex);
                    }
                    case 2 -> System.out.println("Ok!");
                    default -> System.out.println("Invalid Choice!");
                }
            } while (choice != 2);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void selectServerPage() {
        int choice;
        try {
            do {
                request.writeObject(new Request(ReqType.SERVER_LIST));
                wait();
                    System.out.println("1-select server\n2-back");
                    choice = scanner.nextInt();
                    switch (choice) {
                        case 1 -> selectServer();
                        case 2 -> System.out.println("Ok!");
                        default -> System.out.println("Invalid Choice!");
                    }
            } while (choice != 2);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void selectServer() {
        int choice, serverID;
        do {
            System.out.println("1-select server\n2-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    do {
                        System.out.println("Enter server id: ");
                        serverID = scanner.nextInt();
                    } while (serverID > serverList.getServers().size() || serverID < 1);
                    serverPage(serverID);
                }
                case 2 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while (choice > 2 || choice < 1);
    }
    public void newServer() {
        String serverName;
        do {
            System.out.println("Enter server name: ");
            scanner.nextLine();
            serverName = scanner.nextLine();
        } while (serverName.isEmpty());
        try {
            request.writeObject(new NewServerRequest(serverName));
            wait();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private synchronized void newPrivateChat() {
        int choice;
        do {
            System.out.println("1-Enter username\n2-Back to the main page");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter username: ");
                    String username = scanner.next();
                    try {
                        request.writeObject(new NewPrivateChatRequest(username));
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
    private synchronized void addFriend() {
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
    private synchronized void friends()  {
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
        int choice;
        do {
            System.out.println("1-change password\n2-change email\n3-change status\n4-log out\n5-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> changePassword();
                case 2 -> changeEmail();
                case 3 -> changeStatus();
                case 4 -> logOut();
                case 5 -> System.out.println("Ok!");
                default -> System.out.println("Invalid Choice!");
            }
        } while(choice != 5);
    }
    private void changeStatus() {

    }
    private void changePassword() {
        String password;
        do {
            System.out.println("Enter new password: ");
            password = scanner.next();
        } while (password.isEmpty());
        try {
            request.writeObject(new ChangePasswordRequest(password));
            wait();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void changeEmail() {

    }
    private void logOut() {}

    public  static void clearConsole()
    {
        for (int i = 0; i < 50; ++i) System.out.println();

    }

    /**
     * closes the connection with the server
     */
    private void close() {
        try {
            if (request != null)
                request.close();
            if (response != null)
                response.close();
            if (serverConnection != null)
                serverConnection.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instantiates a new client.
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
