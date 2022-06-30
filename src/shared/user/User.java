package shared.user;

import java.io.File;
import java.io.Serializable;

enum UserStatus{
    ONLINE,
    IDLE,
    DO_NOT_DISTURB,
    INVISIBLE
}
public class User implements Serializable {
    private final String username;
    private  String email;
    private String phoneNumber; // optional
    private File Profile; //optional
    private UserStatus status;

    public String getUsername() {
        return username;
    }

    public User(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    //Block
    //Get friends list with their status
    //private chat
    //make server (gets name and can be changed ) _ every body in server can add member Owner and admins can  delete member _ welcome message
    // _ multiple chanels  _ see members in server status
    // Chanel : name _ type ( voice _ text )
    //owner can give roles  : 1_ make chanel 2_ delete chanel 3_ delete member 4_limit access to a chanel 5_ block from server
    //6 _ change server name 7_ see chat history 8_ pin a message
    // send and download files

    //Get user info
    public String userStatus() {
        return username + " : " + status;
    }
}
