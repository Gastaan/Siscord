package shared.requests;

public class NewServerRequest extends Request{
    private final String serverName;
    //constructor
    public NewServerRequest(String serverName) {
        super(ReqType.NEW_SERVER);
        this.serverName = serverName;
    }
    //getters
    public String getServerName() {
        return serverName;
    }
}
