package shared.responses;

public class NewServerResponse extends Response{
    private final boolean success;
    private final int serverID;
    //constructor
    public NewServerResponse(boolean success, int serverID) {
        super(ResponseType.NEW_SERVER);
        this.success = success;
        this.serverID = serverID;
    }
    //toString
    @Override
    public String toString() {
        return "NewServerResponse{" +
                "success=" + success +
                ", serverID=" + serverID +
                '}';
    }
}
