package shared.requests;

public class GetChanelsRequest extends Request{
    private final int serverID;
    //constructor
    public GetChanelsRequest(int serverID) {
        super(ReqType.SERVER_CHANELS);
        this.serverID = serverID;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
}
