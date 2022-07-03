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
    private String phoneNumber;
    private File profilePhoto;
    private UserStatus status;

    public String getUsername() {
        return username;
    }

    public User(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public String userStatus() {
        return username + " : " + status;
    }
}
