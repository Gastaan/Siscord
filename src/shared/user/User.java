package shared.user;

import java.io.File;
import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private  String email;
    private String phoneNumber;
    private File profilePhoto;
    transient private UserStatus status = UserStatus.OFFLINE;

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
    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
