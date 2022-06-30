package shared.responses;

import java.util.ArrayList;

public class GetOutgoingFriendResponse extends Response{
    private final ArrayList<String> outgoingFriendRequests;
    //constructor
    public GetOutgoingFriendResponse(ArrayList<String> outgoingFriendRequests) {
        super(ResType.GET_OUTGOING_FRIEND);
        this.outgoingFriendRequests = outgoingFriendRequests;
    }
    //getters
    public ArrayList<String> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    }
}
