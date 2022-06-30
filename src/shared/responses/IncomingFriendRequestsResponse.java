package shared.responses;

import java.util.ArrayList;
import java.util.HashSet;

public class IncomingFriendRequestsResponse extends Response{
    private  final ArrayList<String> incomingFriendRequests;
    //constructor
    public IncomingFriendRequestsResponse(HashSet<String> incomingFriendRequests) {
        super(ResType.INCOMING_FRIEND_REQUESTS);
        this.incomingFriendRequests = new ArrayList<>(incomingFriendRequests);
    }
    //getters
    public ArrayList<String> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }
}
