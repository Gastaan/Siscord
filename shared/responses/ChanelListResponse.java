package shared.responses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author saman hazemi
 */
public class ChanelListResponse extends Response{
    private final int serverID;
    private final HashMap<String, Boolean> chanelNames;
    //constructor
    public ChanelListResponse(int serverID, HashMap<String, Boolean> chanelNames) {
        super(ResponseType.CHANEL_LIST);
        this.serverID = serverID;
        this.chanelNames = chanelNames;
    }
    //getters
    public int getServerID() {
        return serverID;
    }
    public ArrayList<String> getChanelNames() {
        return new ArrayList<>(chanelNames.keySet());
    }
    public boolean getChanelType(String chanelName) {
        return chanelNames.get(chanelName);
    }
    //toString
    @Override
    public String toString() {
        int index = 1;
        StringBuilder value = new StringBuilder("Chanels: \n");
        for(String chanelName : chanelNames.keySet())
            value.append(index++).append("- ").append(chanelName).append(" : ").append(chanelNames.get(chanelName) ? "text" : "music").append("\n");
        return value.toString();
    }
}
