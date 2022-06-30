package shared.responses.addfriend;

import shared.responses.ResType;
import shared.responses.Response;

public class AddFriendResponse extends Response {
    private final AddFriendResponseStatus responseStatus;
    private final String friendUsername;
    public AddFriendResponse(AddFriendResponseStatus responseType, String friendUsername) {
        super(ResType.ADD_FRIEND);
        this.responseStatus = responseType;
        this.friendUsername = friendUsername;
    }
    //toString
    @Override
    public String toString() {
        return "AddFriendResponse{" +
                "responseStatus=" + responseStatus +
                ", friendUsername='" + friendUsername + '\'' +
                '}';
    }
}
