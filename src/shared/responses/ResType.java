package shared.responses;

import java.io.Serializable;

public enum ResType implements Serializable {
    LOGIN,
    SIGNUP,
    LIST,
    PRIVATE_CHAT,
    NOTIFICATION,
    NEW_MESSAGE,
    INCOMING_FRIEND_REQUESTS,
    ADD_FRIEND,
    GET_FRIENDS_LIST,
    GET_OUTGOING_FRIEND,
    GET_BLOCKED_USERS,
    UNBLOCK_USER,
    BLOCK_USER,
    NEW_PRIVATE_CHAT,
    PRIVATE_CHAT_REACT,
    NEW_SERVER,
    SERVER_LIST,
    CHANEL_LIST,
    CHANGE_PASSWORD
}
