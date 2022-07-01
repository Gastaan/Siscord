package shared.responses;

import java.io.Serializable;

public enum ResType implements Serializable {
    LOGIN,
    SIGNUP,
    PRIVATE_CHAT_LIST,
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
    PRIVATE_CHAT_REACT
}
