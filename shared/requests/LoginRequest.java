package shared.requests;

public class LoginRequest extends Request{
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        super(RequestType.LOGIN);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
