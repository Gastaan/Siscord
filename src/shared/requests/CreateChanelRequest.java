package shared.requests;

public class CreateChanelRequest extends Request{
    private final int serverId;
    private final String chanelName;
    private final boolean isTextChanel;
    //constructor
    public CreateChanelRequest(int serverId, String chanelName, boolean isTextChanel) {
        super(RequestType.CREATE_CHANEL);
        this.serverId = serverId;
        this.chanelName = chanelName;
        this.isTextChanel = isTextChanel;
    }
    //getters
    public int getServerId() {
        return serverId;
    }
    public String getChanelName() {
        return chanelName;
    }
    public boolean isTextChanel() {
        return isTextChanel;
    }
}
