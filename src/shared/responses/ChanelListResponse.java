package shared.responses;

import java.util.ArrayList;

public class ChanelListResponse extends Response{
    private int serverID;
    private final ArrayList<String> chanelNames;
    //constructor
    public ChanelListResponse(int serverID, ArrayList<String> chanelNames) {
        super(ResType.CHANEL_LIST);
        this.serverID = serverID;
        this.chanelNames = chanelNames;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
    public ArrayList<String> getChanelNames() {
        return chanelNames;
    }
}
