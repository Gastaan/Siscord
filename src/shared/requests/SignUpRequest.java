package shared.requests;

public class SignUpRequest extends Request{
    private final String username;
    private final String password;
    private final String mail;
    private final String phoneNumber;
    //constructor
    public SignUpRequest(String username, String password, String email, String phoneNumber) {
        super(RequestType.SIGN_UP);
        this.username = username;
        this.password = password;
        this.mail = email;
        this.phoneNumber = phoneNumber;
    }
    //getters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getMail() {
        return mail;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
