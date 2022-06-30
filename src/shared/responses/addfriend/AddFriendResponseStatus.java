package shared.responses.addfriend;

import java.io.Serializable;

public enum AddFriendResponseStatus implements Serializable {
    USER_NOT_FOUND,
    ALREADY_FRIENDS,
    FRIEND_REQUEST_SENT,
}
