package shared.responses;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerListResponse extends Response{
    private final HashMap<Integer,String> servers;
    //constructor
    public ServerListResponse(HashMap<Integer, String>servers) {
        super(ResType.SERVER_LIST);
        this.servers = servers;
    }
    //getters
    public ArrayList<String> getServers() {
        return new ArrayList<>(servers.values());
    }
    public int getID(String server) {
        for(Integer id : servers.keySet()) {
            if(servers.get(id).equals(server)) {
                return id;
            }
        }
        return -1;
    }
}
