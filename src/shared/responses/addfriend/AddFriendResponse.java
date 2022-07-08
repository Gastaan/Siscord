package shared.responses.addfriend;

import shared.responses.ResponseType;
import shared.responses.Response;

/**
 * @author saman hazemi
 */
public class AddFriendResponse extends Response {
    private final AddFriendResponseStatus responseStatus;
    private final String friendUsername;
    public AddFriendResponse(AddFriendResponseStatus responseType, String friendUsername) {
        super(ResponseType.ADD_FRIEND);
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
