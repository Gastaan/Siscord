//Ya Ali ii
package client;

import shared.requests.*;
import shared.responses.*;
import shared.responses.ListResponse;
import shared.responses.LoginResponse;
import shared.responses.signup.SignUpResponse;
import shared.user.User;
import shared.user.UserStatus;
import shared.user.data.message.FileMessage;
import shared.user.data.message.Message;
import shared.user.data.message.Reacts;
import shared.user.data.message.TextMessage;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Saman Hazemi
 * @version 1.0
 * This class is the client side of the program.
 */
public class Client {
    //fields
    public static Scanner scanner = new Scanner(System.in);
    private  Socket serverConnection;
    private User user;
    private ObjectInputStream response;
    private ObjectOutputStream request;
    private ResponseHandler responseHandler;
    private ListResponse list;
    private ChatResponse chat;
    private ServerListResponse serverList;
    private ServerMembersResponse serverMembers;
    private ChanelListResponse chanels;
    private SourceDataLine speaker;
    private MusicResponse music;
    private String inVoiceCall = null;
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

    /**
     * constructor of the client class.
     */
    public Client() {
        try {
            serverConnection = new Socket("localhost", 3132); //localhost : 127.0.0.1
            request = new ObjectOutputStream(serverConnection.getOutputStream());
            response = new ObjectInputStream(serverConnection.getInputStream());
            responseHandler = new ResponseHandler();
            listener();
        } catch (IOException e) {
            System.err.println("Can not connect to server!");
            closeConnection();
            System.exit(404);
        }
    }
    //methods

    /**
     * This method is used to listen to the server and handle the responses.
     */
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

    /**
     * This method is used to handle the responses.
     * @param response The response from the server.
     */
    private synchronized void handleResponse(Response response) {
        ResponseType type = response.getResType();
        if(type == ResponseType.SIGNUP) {
            user = responseHandler.signUpResponse((SignUpResponse) response);
            notify();
        }
        else if(type == ResponseType.LOGIN) {
            user = responseHandler.loginResponse((LoginResponse) response);
            notify();
        }
        else if(type == ResponseType.LIST) {
            list = (ListResponse) response;
            notify();
        }
        else if(type == ResponseType.CHAT) {
            chat = (ChatResponse)response;
            if(chat != null) {
                responseHandler.chatResponse(chat);
                downLoadFileMessages();
            }
            else
                System.out.println(ANSI_BLACK + "No chat found!" + ANSI_RESET);
            notify();
        }
        else if(type == ResponseType.NOTIFICATION) {
            if(user.getStatus() !=  UserStatus.DO_NOT_DISTURB) {
                System.out.println(((Notification) response).getDescription());
                System.out.flush();
            }
        }
        else if(type  == ResponseType.ADD_FRIEND) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.UNBLOCK_USER) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.BLOCK_USER) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.NEW_MESSAGE) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.PRIVATE_CHAT_REACT)  {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.NEW_PRIVATE_CHAT) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.NEW_SERVER) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.SERVER_LIST) {
            serverList = (ServerListResponse) response;
            serverList.printServer();
            notify();
        }
        else if(type == ResponseType.CHANEL_LIST) {
            chanels = (ChanelListResponse) response;
            System.out.println(ANSI_CYAN + chanels + ANSI_RESET);
            notify();
        }
        else if(type == ResponseType.CHANGE_PASSWORD || type == ResponseType.PIN_MESSAGE) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.CHANGE_EMAIL || type == ResponseType.CHANGE_PHONE_NUMBER) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.SERVER_MEMBERS) {
            serverMembers = (ServerMembersResponse) response;
            System.out.println(ANSI_CYAN + response + ANSI_RESET);
            notify();
        }
        else if(type == ResponseType.VOICE) {
            VoiceResponse voice = (VoiceResponse) response;
            if(voice.getUsername().equals(inVoiceCall))
                speaker.write(voice.getBufferedVoice(), 0, voice.getBytesRead());
        }
        else if(response instanceof BooleanResponse) {
            System.out.println(response);
            notify();
        }
        else if(type == ResponseType.MUSIC) {
            music = (MusicResponse) response;
            notify();
        }
        else  {
            System.out.println(response);
            notify();
        }
    }

    /**
     * Main Page of the client.
     */
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
            closeConnection();
        }
    }

    /**
     * This method is used to log in the user.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If interrupted while waiting for the response.
     */
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

    /**
     * This method is used to sign up the user.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If interrupted while waiting for the response.
     */
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

    /**
     * The home page of the client.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If interrupted while waiting for the response.
     */
    private void homePage() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_YELLOW + "1- private chats\n2- servers\n3- friends\n4- add friend\n5-incoming friend requests\n6-pending requests\n7-blocked users\n8-change status\n9- setting\n10- logout" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> privateChats();
                case 2 -> servers();
                case 3 -> friends();
                case 4 -> addFriend();
                case 5 -> incomingFriendRequests();
                case 6 -> outGoingFriendRequests();
                case 7 -> blockedUsers();
                case 8 -> changeStatus();
                case 9 -> setting();
                case 10 -> System.out.println(ANSI_BLUE + "Bye Bye!" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid Choice!" + ANSI_RESET);
            }
        } while (choice != 10);
        logOut();
    }

    /**
     * This method is used to log out.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If interrupted while waiting for the response.
     */
    private void logOut() throws IOException, InterruptedException {
        user = null;
         ListResponse list = null;
         ChatResponse chat = null;
         ServerListResponse serverList = null;
         ServerMembersResponse serverMembers = null;
         ChanelListResponse chanels = null;
         request.writeObject(new Request(RequestType.LOGOUT));
         wait(10000);
    }

    /**
     * This method is used to see outgoing friend requests and cancel them.
     */
    private synchronized void outGoingFriendRequests() throws IOException, InterruptedException {
        int choice;
            do {
                request.writeObject(new Request(RequestType.GET_OUTGOING_FRIEND));
                wait();
                System.out.println(ANSI_PURPLE +"1-cancel\n2-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> cancelFriendRequest();
                    case 2 ->  System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice != 2);
        }

    /**
     * This method is used to cancel a friend request.
     * @throws IOException If an I/O error occurs while sending the request.
     */
    private void cancelFriendRequest() throws IOException {
        String username = list.getList().get(selectFromList()- 1);
        if(username != null)
          request.writeObject(new StringRequest(username , RequestType.CANCEL_FRIEND_REQUEST));
        }

    /**
     * This method is used to block or unblock a user.
     * @throws InterruptedException If interrupted while waiting for the response.
     * @throws IOException If an I/O error occurs while sending the request.
     */
    private synchronized void blockedUsers() throws InterruptedException, IOException {
        int choice;
            do {
                request.writeObject(new Request(RequestType.GET_BLOCKED_USERS));
                wait();
                System.out.println(ANSI_PURPLE + "1-select\n2-block a user\n3-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> selectBlockedUser();
                    case 2 -> blockUser();
                    case 3 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice != 3);
    }

    /**
     * This method is used to unblock a user.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If interrupted while waiting for the response.
     */
    private synchronized void selectBlockedUser() throws IOException, InterruptedException {
        int blockedUserIndex, choice;
        System.out.println(ANSI_WHITE + "Select a blocked user: " + ANSI_RESET);
        blockedUserIndex = selectFromList();
        if(blockedUserIndex != -1) {
            do {
                System.out.println(ANSI_PURPLE + "1-unblock\n2-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> {
                        request.writeObject(new StringRequest(list.getList().get(blockedUserIndex - 1), RequestType.UNBLOCK_USER));
                        wait();
                    }
                    case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice > 2 || choice < 1);
        }
    }

    /**
     * This method is used to block a user.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If interrupted while waiting for the response.
     */
    private void blockUser() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_PURPLE + "1-enter username\n2-back" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> {
                    System.out.println(ANSI_WHITE +"Enter username: " + ANSI_RESET);
                    String username = scanner.next();
                    request.writeObject(new StringRequest(username, RequestType.BLOCK_USER));
                    wait();
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice != 2);
    }

    /**
     * This method is used to see incoming friend requests and accept or reject them.
     */
    private synchronized void incomingFriendRequests() throws InterruptedException, IOException {
        int choice;
            do {
                request.writeObject(new Request(RequestType.GET_INCOMING_FRIEND_REQUESTS));
                wait();
                System.out.println(ANSI_PURPLE + "1-select request\n2-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> selectRequest();
                    case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice != 2);
        }

    /**
     * This method is used to select a friend request and accept or reject it.
     * @throws IOException If an I/O error occurs while sending the request.
     */
    private void selectRequest() throws IOException {
        int requestIndex, choice;
        Boolean accepted = null;
        System.out.println(ANSI_WHITE + "Select a request: " + ANSI_RESET);
        requestIndex = selectFromList();
        if(requestIndex != -1) {
            do {
                System.out.println(ANSI_PURPLE + "1-accept\n2-decline\n3-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> accepted = true;
                    case 2 -> accepted = false;
                    case 3 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice > 3 || choice < 1);
        }
        if(accepted != null)
            request.writeObject(new FriendRequestAnswerRequest(list.getList().get(requestIndex - 1), accepted));
    }

    /**
     * Private chat page.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted.
     */
    private synchronized void privateChatPage() throws IOException, InterruptedException {
        int choice, chatIndex;
        chatIndex = selectFromList();
        if(chatIndex != -1) {
            do {
                request.writeObject(new PlaceholderRequest(RequestType.CHAT_REQUEST, list.getList().get(chatIndex - 1)));
                wait();
                System.out.println(ANSI_YELLOW + "1-sendMessage\n2-React\n3-pin\n4-voice call\n5-pinned messages\n6-back to home page" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> sendMessage();
                    case 2 -> react();
                    case 3 -> pin();
                    case 5 -> showPinnedMessages();
                    case 4 -> voiceCall();
                    case 6 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice != 6);
        }
        }

    /**
     * Pins a message.
     * @throws InterruptedException If interrupted while waiting for a response.
     * @throws IOException If an I/O error occurs while sending a request.
     */
    private void pin() throws InterruptedException, IOException {
            int choice;
                do {
                    System.out.println(ANSI_PURPLE + "1-pin\n2-back" + ANSI_RESET);
                    try {
                        choice = scanner.nextInt();
                    }
                    catch (InputMismatchException e) {
                        scanner.nextLine();
                        choice = -1;
                    }
                    switch (choice) {
                        case 1 -> {
                            int messageIndex;
                            do {
                                messageIndex = selectMessage();
                                if(messageIndex != -1)
                                    if (chat.getMessages().get(messageIndex - 1).isPinned())
                                        System.out.println(ANSI_RED + "Message is already pinned" + ANSI_RESET);
                            } while(chat.getMessages().get(messageIndex - 1).isPinned() && messageIndex != -1);
                            if(messageIndex != -1) {
                                chat.getMessages().get(messageIndex - 1).setPinned();
                                request.writeObject(new StringChanelRequest(RequestType.PIN_MESSAGE, chat.getMessages().get(messageIndex - 1).getTime(), chat.getPlaceholder()));
                                wait();
                            }
                        }
                        case 2 -> System.out.println(ANSI_BLUE + "Ok" + ANSI_RESET);
                        default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                    }
                } while (choice != 2);
        }

    /**
     * prints the pinned messages.
     */
    private void showPinnedMessages() {
            chat.printPinnedMessages();
        }

    /**
     * The text chat page.
     * @param chanelName The name of the channel.
     * @throws IOException If an I/O error occurs while sending a request.
     * @throws InterruptedException If interrupted while waiting for a response.
     */
    private void textChanelPage(String chanelName) throws IOException, InterruptedException {
        int choice;
            do {
                request.writeObject(new PlaceholderRequest(RequestType.CHAT_REQUEST, String.valueOf(chanels.getServerID()) ,  chanelName));
                wait(30000);
                System.out.println("1-sendMessage\n2-React\n3-pin\n4-see pinned messages\n5-delete chanel\n6-limit members\n7-back");
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> sendMessage();
                    case 2 -> react();
                    case 3 -> pin();
                    case 4 -> showPinnedMessages();
                    case 5 -> deleteChanel();
                    case 6 -> limitMembers();
                    case 7 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice != 7 && choice != 5);
        }
    /**
     * This method is used to delete a chanel.
     */
    private void deleteChanel() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_WHITE + "Are you sure you want to delete this chanel?\n1-yes\n2-no" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> {
                    request.writeObject(new PlaceholderRequest(RequestType.DELETE_CHANEL, String.valueOf(chanels.getServerID()), chat.getPlaceholder()[1]));
                    wait(30000);
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice < 1 || choice > 2);
    }
    /**
     * This method is used to limit access to a chanel.
     * @throws IOException If an I/O error occurs while sending a request.
     * @throws InterruptedException If interrupted while waiting for a response.
     */
     private void limitMembers() throws IOException, InterruptedException {
         int choice;
         do {
             System.out.println(ANSI_WHITE + "1-set limit\n2-remove limit\n3-add access\n4-back" + ANSI_RESET);
             try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
             }
             switch (choice) {
                    case 1 -> {
                        request.writeObject(new PlaceholderRequest(RequestType.SET_LIMIT, String.valueOf(chanels.getServerID()), chat.getPlaceholder()[1]));
                        wait(30000);
                    }
                    case 2 -> {
                        request.writeObject(new PlaceholderRequest(RequestType.REMOVE_LIMIT, String.valueOf(chanels.getServerID()), chat.getPlaceholder()[1]));
                        wait(30000);
                    }
                    case 3 -> {
                        request.writeObject(new ServerIDRequest(RequestType.SERVER_MEMBERS, chanels.getServerID()));
                        wait();
                        String member = selectMember();
                        if(member != null) {
                            request.writeObject(new StringChanelRequest(RequestType.ADD_ACCESS, member,  String.valueOf(chanels.getServerID()), chat.getPlaceholder()[1]));
                            wait(30000);
                        }
                    }
                    case 4 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
             }
         } while (choice != 4);
    }

    /**
     * Send message to a user.
     * @throws IOException If an I/O error occurs while sending the message.
     * @throws InterruptedException If interrupted while waiting for the server response.
     */
    private synchronized void sendMessage() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_WHITE + "1-text\n2-file\n3-back" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            }
            catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> {
                    request.writeObject(new PlaceholderRequest(RequestType.IS_TYPING, chat.getPlaceholder()));
                    wait();

                    System.out.println(ANSI_WHITE + "Enter your message: " + ANSI_RESET);
                    scanner.nextLine();
                    String message = scanner.nextLine();
                    Message newMessage = new TextMessage(user.getUsername(), message);
                    request.writeObject(new NewMessageRequest(newMessage, chat.getPlaceholder()));
                    wait();
                }
                case 2 -> sendFileMessages();
            }
        } while (choice < 1 || choice > 3);
    }

    /**
     * Send file message.
     * @param finalFilePath The path of the file.
     */
    private void uploadFileMessage(String finalFilePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileMessage newMessage = new FileMessage(user.getUsername(), Files.readAllBytes(Paths.get(finalFilePath)), finalFilePath);
                    synchronized (request) {
                        request.writeObject(new NewMessageRequest(newMessage, chat.getPlaceholder()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * Send file message.
     */
    private void sendFileMessages() {
        String filePath;
        do {
            System.out.println(ANSI_WHITE + "Enter file path: " + ANSI_RESET);
            filePath = scanner.next();
            if(!Files.exists(Paths.get(filePath)))
                System.out.println(ANSI_RED + "File not found!" + ANSI_RESET);
        } while (!Files.exists(Paths.get(filePath)));
        uploadFileMessage(filePath);
    }

    /**
     * Download file messages.
     */
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
    //This method sends voice to server using source data line .
    private void voiceCall() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try {
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(format);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        speaker.start();
        inVoiceCall = chat.getPlaceholder()[0];
        new Thread(new Runnable() {
         @Override
         public void run() {
             try {
                 TargetDataLine microphone;
                 DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                 microphone = (TargetDataLine) AudioSystem.getLine(info);
                 microphone.open(format);
                 int CHUNK_SIZE = 1024;
                 byte[] data = new byte[microphone.getBufferSize() / 5];
                 microphone.start();
                 int bytesRead = 0, count;
                 while ((count = microphone.read(data, 0, CHUNK_SIZE)) > 0 && inVoiceCall != null) {
                     bytesRead += count;
                     request.writeObject(new Voice(data, bytesRead, chat.getPlaceholder()[0]));
                 }
                 System.out.println("Closed!");
                 microphone.close();
             } catch (Exception e) {
                 System.out.println("Error");
                 e.printStackTrace();
             }
         }
     }).start();
        do {
            System.out.println("Enter zero to stop voice call");
        } while (scanner.nextInt() != 0);
        speaker.drain();
        speaker.close();
        inVoiceCall = null;
    } //TODO : DEBUGGING
    /**
     * React to a message.
     * @throws IOException If an I/O error occurs while sending the message.
     * @throws InterruptedException If interrupted while waiting for the server response.
     */
    private void react() throws InterruptedException, IOException {
        int choice, reactChoice;
        do {
                System.out.println(ANSI_PURPLE + "1-select message\n2-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 ->  {
                        int messageIndex = selectMessage();
                        if(messageIndex != -1) {
                            Message message = chat.getMessages().get(messageIndex - 1);
                            Reacts reaction = null;
                            do {
                                System.out.println(ANSI_WHITE + "Enter your reaction: \n1-like\n2-dislike\n3-lol" + ANSI_RESET);
                                try {
                                    reactChoice = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    scanner.nextLine();
                                    reactChoice = -1;
                                }
                                switch (reactChoice) {
                                    case 1 -> reaction = Reacts.LIKE;
                                    case 2 -> reaction = Reacts.DISLIKE;
                                    case 3 -> reaction = Reacts.LOL;
                                    default -> System.out.println(ANSI_RED + "Invalid !" + ANSI_RESET);
                                }
                            }
                            while (reactChoice > 3 || reactChoice < 1);
                            request.writeObject(new ReactRequest(message.getTime(), reaction, chat.getPlaceholder()));
                            wait();
                        }
                    }
                    case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
        } while(choice != 2);
    }
    private int selectMessage() {
    int messageIndex = -1;
    chat.printAllMessages();
    if(chat.getMessages().size() != 0) {
        do {
            System.out.println(ANSI_WHITE + "Enter message index: " + ANSI_RESET);
            try {
                messageIndex = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                messageIndex = -1;
            }
            if (messageIndex > chat.getMessages().size() || messageIndex < 1)
                System.out.println(ANSI_RED + "Invalid!" + ANSI_RESET);
        } while (messageIndex < 1 || messageIndex > chat.getMessages().size());
    }
    else
        System.out.println(ANSI_BLACK + "No messages!" + ANSI_RESET);
        return messageIndex;
    }
    /**
     * Private chats page
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private synchronized void privateChats() throws IOException, InterruptedException {
        int choice;
            do {
                request.writeObject(new Request(RequestType.PRIVATE_CHAT_LIST));
                wait();
                System.out.println(ANSI_YELLOW + "1-select chat\n2-new chat\n3-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                }
                catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> privateChatPage();
                    case 2 -> newPrivateChat();
                    case 3 -> System.out.println(ANSI_BLUE + "Ok" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid Choice!" + ANSI_RED);
                }
            } while (choice != 3);
    }

    /**
     * Select an element from the list.
     * @return The index of the selected element.
     */
    private int selectFromList() {
        int  index = -1;
        if(list.getList().size() != 0)
        do {
            list.printList();
            System.out.println(ANSI_WHITE + "Enter index: " + ANSI_RESET);
            try {
                index = scanner.nextInt();
            }
            catch (InputMismatchException e) {
                scanner.nextLine();
                index = -1;
            }
        } while ((index > list.getList().size() || index < 1));
        else
            System.out.println(ANSI_BLACK + "The list is empty!" + ANSI_RESET);
        return index;
    }
    private void musicChanel(String chanelName) throws InterruptedException, IOException {
        int choice;
        do {
            System.out.println("1-select music\n2-back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    request.writeObject(new Request(RequestType.MUSIC_LIST));
                    wait();
                    int musicIndex = selectFromList();
                    if (musicIndex != -1) {
                        request.writeObject(new StringRequest(list.getList().get(musicIndex - 1), RequestType.MUSIC_PLAY));
                        wait();
                        Thread musicPlayer = new Thread(music);
                        musicPlayer.start();
                        musicPlayer.join();
                    }
                }
                case 2 -> System.out.println(ANSI_BLUE + "Ok" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid Choice!" + ANSI_RESET);
            }
        } while (choice != 2);
    }

    /**
     * Servers page.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void servers() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_PURPLE + "1-select server\n2-new server\n3-back" + ANSI_RESET);
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> selectServerPage();
                case 2 -> newServer();
                case 3 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice != 3);
    }

    /**
     * Server page.
     * @param serverID The index of the server in the list.
     */
    private void serverPage(int serverID) throws InterruptedException, IOException {
        int choice;
            do {
                request.writeObject(new ServerIDRequest(RequestType.SERVER_CHANELS , serverID));
                wait();
                System.out.println(ANSI_WHITE + "1-chanels\n2-add friend\n3-members\n4-setting\n5-leave server\n6-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> chanels();
                    case 2 -> addFriendToServer();
                    case 3 -> members();
                    case 4 -> serverSetting();
                    case 5 -> leaveServer();
                    case 6 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice != 6 && choice != 5);
    }

    /**
     * Server setting page.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void serverSetting() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_WHITE + "1-change name\n2-back" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 ->  {
                    System.out.println(ANSI_WHITE + "Enter new name: " + ANSI_RESET);
                    scanner.nextLine();
                    String name = scanner.nextLine();
                    request.writeObject(new  ChangeServerNameRequest(chanels.getServerID(), name));
                    wait();
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while(choice < 1 || choice > 2);
    }

    /**
     * This method is used to leave a server.
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     * @throws IOException if an I/O error occurs while sending a request to the server
     */
    private void leaveServer() throws InterruptedException, IOException {
        int choice;
        do {
            System.out.println("Are you sure?\n1-yes\n2-no");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    request.writeObject(new ServerIDRequest(RequestType.LEAVE_SERVER ,chanels.getServerID()));
                    wait();
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice < 1 || choice > 2);
    }

    /**
     * The members page.
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     * @throws IOException if an I/O error occurs while sending a request to the server
     */
    private void members() throws InterruptedException, IOException {
        int choice;
        do {
            request.writeObject(new ServerIDRequest(RequestType.SERVER_MEMBERS, chanels.getServerID()));
            wait();
            System.out.println(ANSI_WHITE + "1-kick member\n2-block user\n3-give role\n4-back" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> kick();
                case 2 -> blockUserFromServer();
                case 3 -> giveRole();
                case 4 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice != 4);
    }

    /**
     * This method is used to kick a member from a server.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void kick() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println("1-kick member\n2-back");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> {
                    String member = selectMember();
                    if(member != null) {
                        request.writeObject(new ServerMemberRequest(RequestType.KICK_MEMBER, chanels.getServerID(), member));
                        wait();
                    }
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice < 1 || choice > 2);
    }

    /**
     * This method is used to block a user from a server.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void blockUserFromServer() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println("1-block member\n2-back");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> {
                    String member = selectMember();
                    if(member != null) {
                        request.writeObject(new ServerMemberRequest(RequestType.BLOCK_MEMBER, chanels.getServerID(), member));
                        wait();
                    }
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice < 1 || choice > 2);
    }

    /**
     * This method is used to give a role to a member.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void giveRole() throws IOException, InterruptedException {
    int choice;
        do {
            System.out.println("1-give role\n2-back");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> {
                    int roleIndex;
                    do {
                        System.out.println("1-creat chanel\n2-delete chanel\n3-kick member\n4-limit member access\n5-block member\n6-change server name\n7-pin message");
                        try {
                            roleIndex = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            scanner.nextLine();
                            roleIndex = -1;
                        }
                    } while (roleIndex < 1 || roleIndex > 7);
                    String member = selectMember();
                    if(member != null) {
                        request.writeObject(new ServerMemberRequest(RequestType.GIVE_ROLE, chanels.getServerID(), member, roleIndex));
                        wait(15000);
                    }
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice < 1 || choice > 2);
    }

    /**
     * This method is used to select a member from the list of members.
     * @return the name of the member selected by the client
     */
    private String selectMember() {
        String memberName;
        if(serverMembers.getMembers().size() == 1) {
            System.out.println(ANSI_BLACK + "No members" + ANSI_RESET);
            return null;
        }
        do {
            System.out.println(ANSI_WHITE + "Enter member name: " + ANSI_RESET);
            memberName = scanner.next();
        } while (!serverMembers.getMembers().contains(memberName));
        return memberName;
    }
    /**
     * Add a friend to a server.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void addFriendToServer() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println("1-select a friend to add to the server\n2-back");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            switch (choice) {
                case 1 -> {
                    request.writeObject(new Request(RequestType.GET_FRIENDS_LIST));
                    wait(15000);
                    System.out.println("Friends :");
                    int index = selectFromList();
                    if(index != -1) {
                        request.writeObject(new ServerMemberRequest(RequestType.ADD_FRIEND_TO_SERVER, chanels.getServerID(), list.getList().get(index - 1).substring(0, list.getList().get(index - 1).indexOf(" "))));
                        wait();
                    }
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while(choice != 2);
    }

    /**
     * Chanels menu.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private synchronized void chanels() throws IOException, InterruptedException {
        int choice;
            do {
                request.writeObject(new ServerIDRequest(RequestType.SERVER_CHANELS , chanels.getServerID()));
                wait();
                System.out.println(ANSI_PURPLE + "1-select chanel\n2-create chanel\n3-back" + ANSI_RESET);
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    choice = -1;
                }
                switch (choice) {
                    case 1 -> {
                        int chanelIndex;
                        if(chanels.getChanelNames().size() == 0)
                            System.out.println(ANSI_BLACK + "No chanels" + ANSI_RESET);
                        else {
                            ArrayList<String> chanelNames = chanels.getChanelNames();
                            do {
                                System.out.println("Enter chanel index: ");
                                chanelIndex = scanner.nextInt();
                            } while (chanelIndex > chanelNames.size() || chanelIndex < 1);
                            String chanelName = chanelNames.get(chanelIndex - 1);
                            if (!chanels.getChanelType(chanelName))
                                musicChanel(chanelName);
                            else
                                textChanelPage(chanelName);
                        }
                    }
                    case 2 -> createChanel();
                    case 3 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                    default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                }
            } while (choice != 3);
    }

    /**
     * Create a new chanel.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void createChanel() throws IOException, InterruptedException {
        System.out.println(ANSI_WHITE + "Enter chanel name: " + ANSI_RESET);
        String chanelName = scanner.next();
        int choice;
        do {
            System.out.println("1-Voice Chanel\n2-Text Chanel");
            try {
                choice = scanner.nextInt();
            }catch (InputMismatchException e) {
                scanner.nextLine();
                choice = -1;
            }
            if(choice < 1 || choice > 2)
                System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
        } while(choice < 1 || choice > 2);
        request.writeObject(new CreateChanelRequest(chanels.getServerID(), chanelName, choice == 2));
        wait(10000);
    }
    /**
     * Select a server from the list.
     * @throws IOException if an I/O error occurs while sending a request to the server
     * @throws InterruptedException if the thread is interrupted while waiting for a response from the server
     */
    private void selectServerPage() throws IOException, InterruptedException {
        int choice;
            do {
                request.writeObject(new Request(RequestType.SERVER_LIST));
                wait();
                    System.out.println(ANSI_PURPLE + "1-select server\n2-back" + ANSI_RESET);
                    try {
                        choice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        scanner.nextLine();
                        choice = -1;
                    }
                    switch (choice) {
                        case 1 -> selectServer();
                        case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                        default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
                    }
            } while (choice != 2);
    }

    /**
     * Select a server from the list.
     */
    private void selectServer() throws IOException, InterruptedException {
        int  serverIndex;
        if(serverList.getServers().size() == 0) {
            System.out.println(ANSI_BLACK + "No servers" + ANSI_RESET);
            return;
        }
        do {
            serverList.printServer();
            System.out.println("Enter server index: ");
            serverIndex = scanner.nextInt();
        } while (serverIndex > serverList.getServers().size() || serverIndex < 1);
        serverPage(serverList.getServers().get(serverIndex - 1));
    }

    /**
     * This method is used to create a new server.
     */
    public void newServer() throws IOException, InterruptedException {
        String serverName;
        do {
            System.out.println("Enter server name: ");
            scanner.nextLine();
            serverName = scanner.nextLine();
        } while (serverName.isEmpty());
        request.writeObject(new StringRequest(serverName, RequestType.NEW_SERVER));
        wait();
    }

    /**
     * This method is used to create a new private chat.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If the thread is interrupted while waiting for the response.
     */
    private synchronized void newPrivateChat() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_PURPLE + "1-Enter a username\n2-Back to the home page" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            }
            catch (InputMismatchException e) {
                choice = -1;
                scanner.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    System.out.println(ANSI_WHITE + "Enter username: " + ANSI_RESET);
                    String username = scanner.next();
                    request.writeObject(new StringRequest(username, RequestType.NEW_PRIVATE_CHAT));
                    wait();
                }
                case 2 -> System.out.println(ANSI_BLUE + "Ok" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid Choice!" + ANSI_RESET);
            }
        } while(choice != 2);
    }

    /**
     * This method is used to add a friend
     */
    private synchronized void addFriend() throws InterruptedException, IOException {
        int choice;
        do {
            System.out.println(ANSI_PURPLE + "1-Enter username\n2-Back to the main page" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = -1;
                scanner.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    System.out.println(ANSI_WHITE + "Enter username: " + ANSI_RESET);
                    String username = scanner.next();
                    request.writeObject(new StringRequest(username, RequestType.ADD_FRIEND));
                    wait();
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while(choice != 2);
    }

    /**
     * This method is used to get the list of friends.
     * @throws IOException If an I/O error occurs while sending the request.
     */
    private synchronized void friends() throws IOException, InterruptedException {
        int choice;
        do {
            request.writeObject(new Request(RequestType.GET_FRIENDS_LIST));
            wait();
            System.out.println(ANSI_PURPLE + "1-select friend\n2-Back to the main page" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = -1;
                scanner.nextLine();
            }
            switch (choice) {
                case 1 -> removeFriend();
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice != 2);
    }

    /**
     * This method is used to select a friend to remove.
     * @throws IOException if an I/O error occurs while sending the request.
     */
    private void removeFriend() throws IOException {
        int friendIndex, choice;
        do {
            System.out.println(ANSI_WHITE + "1-remove\n2-back" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = -1;
                scanner.nextLine();
            }
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter friend index: ");
                    friendIndex = selectFromList();
                    if(friendIndex != -1) {
                        String friend = list.getList().get(friendIndex - 1).substring(0, list.getList().get(friendIndex - 1).indexOf(" "));
                        request.writeObject(new StringRequest(friend, RequestType.REMOVE_FRIEND));
                    }
                }
                case 2 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println( ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while (choice > 2 || choice < 1);
    }

    /**
     * This method is used to return the status.
     * @param status The new status of the user.
     * @return The new status of the user.
     */
    private UserStatus findStatus(String status) {
        return switch (status) {
            case "1" -> UserStatus.ONLINE;
            case "2" -> UserStatus.IDLE;
            case "3" -> UserStatus.DO_NOT_DISTURB;
            case "4" -> UserStatus.OFFLINE;
            default -> null;
        };
    }

    /**
     * This method is used to set the status of the user.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If the thread is interrupted while waiting for the response.
     */
    private void changeStatus() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println("1-online\n2-Idle\n3-Do not disturb\n4-invisible\n5-back");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = -1;
                scanner.nextLine();
            }
            if(choice > 5 || choice < 1)
                System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            else if(choice != 5){
                user.setStatus(findStatus(String.valueOf(choice)));
                request.writeObject(new StringRequest(String.valueOf(choice), RequestType.CHANGE_STATUS));
                wait();
            }
        }
        while (choice > 5 || choice < 1);
    }

    /**
     * Settings page.
     */
    private void setting() throws IOException, InterruptedException {
        int choice;
        do {
            System.out.println(ANSI_WHITE + "1-change password\n2-change mail\n3-change phone number\n4-back" + ANSI_RESET);
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                choice = -1;
                scanner.nextLine();
            }
            switch (choice) {
                case 1 -> changePassword();
                case 2 -> changeEmail();
                case 3 -> changePhoneNumber();
                case 4 -> System.out.println(ANSI_BLUE + "OK" + ANSI_RESET);
                default -> System.out.println(ANSI_RED + "Invalid" + ANSI_RESET);
            }
        } while(choice != 4);
    }

    /**
     * This method is used to change the password.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws IOException if an I/O error occurs while sending the request.
     */
    private void changePassword() throws InterruptedException, IOException {
        String password;
        System.out.println("Enter new password: ");
        password = scanner.next();
        request.writeObject(new StringRequest(password, RequestType.CHANGE_PASSWORD));
        wait();
    }

    /**
     * This method is used to change the email.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws IOException if an I/O error occurs while sending the request.
     */
    private void changeEmail() throws InterruptedException, IOException {
        String password;
        System.out.println("Enter new email: ");
        password = scanner.next();
        request.writeObject(new StringRequest(password, RequestType.CHANGE_EMAIL));
        wait();
    }

    /**
     * This method is used to select a friend to remove.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws IOException if an I/O error occurs while sending the request.
     */
    private void changePhoneNumber() throws InterruptedException, IOException {
        String newPhoneNumber;
        System.out.println("Enter new phone number: ");
        newPhoneNumber = scanner.next();
        request.writeObject(new StringRequest(newPhoneNumber, RequestType.CHANGE_PHONE_NUMBER));
        wait();
    }
    /**
     * closes the connection with the server
     */
    private void closeConnection() {
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
