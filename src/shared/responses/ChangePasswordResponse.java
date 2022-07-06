package shared.responses;

public class ChangePasswordResponse extends Response{
    private final boolean success;
    //constructor
    public ChangePasswordResponse(boolean success) {
        super(ResType.CHANGE_PASSWORD);
        this.success = success;
    }
    //toString
    @Override
    public String toString() {
        return "ChangePasswordResponse{" +
                "success=" + success +
                '}';
    }
}
