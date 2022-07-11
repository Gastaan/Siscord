package shared.requests;

/**
 * This class is used to send a request to change the name of the server.
 */
public class ChangeServerNameRequest extends Request{
    private final int serverID;
    private final String newName;
    //constructor

    /**
     * This constructor is used to create a new ChangeServerNameRequest.
     * @param serverID The ID of the server to change the name of.
     * @param newName The new name of the server.
     */
    public ChangeServerNameRequest(int serverID, String newName) {
        super(RequestType.CHANGE_SERVER_NAME);
        this.serverID = serverID;
        this.newName = newName;
    }
    //getters

    /**
     * This method is used to get the ID of the server to change the name of.
     * @return The ID of the server to change the name of.
     */
    public int getServerID() {
        return serverID;
    }
    /**
     * This method is used to get the new name of the server.
     * @return The new name of the server.
     */
    public String getNewName() {
        return newName;
    }
}
