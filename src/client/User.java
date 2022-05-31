package client;

import java.io.File;
enum UserStatus{
    ONLINE,
    IDLE,
    DO_NOT_DISTURB,
    INVISIBLE
}
public class User {
    private String username;
    private String email;
    private String phoneNumber; // optional
    private File Profile; //optional
    public String getUsername() {
        return username;
    }
    public void homePage() {

    }
}
