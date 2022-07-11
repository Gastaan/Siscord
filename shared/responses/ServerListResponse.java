package shared.responses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author saman hazemi
 * This class is used to send  list of servers to the client.
 */
public class ServerListResponse extends Response{
    private final HashMap<Integer,String> servers;
    //constructor

    /**
     * Constructor for the ServerListResponse class.
     * @param servers The list of servers to be sent to the client.
     */
    public ServerListResponse(HashMap<Integer, String>servers) {
        super(ResponseType.SERVER_LIST);
        this.servers = servers;
    }

    /**
     * Prints the list of servers.
     */
    public void printServer() {
        int index = 1;
        for (Integer i : servers.keySet()) {
            System.out.println(index++ + "- id: " + i + " " + servers.get(i));
        }
    }

    /**
     * Getter for the list of servers.
     * @return The list of servers.
     */
    public ArrayList<Integer> getServers() {
        return new ArrayList<>(servers.keySet());
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
