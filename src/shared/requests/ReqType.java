package shared.requests;

import java.io.Serializable;

public enum ReqType implements Serializable {
    LOGIN,
    SIGN_UP,
    PRIVATE_CHAT,
    PRIVATE_CHAT_LIST,
    CHAT_REQUEST,
    PRIVATE_CHAT_REACT,
    PRIVATE_CHAT_IS_TYPING,
    NEW_PRIVATE_CHAT,
    NEW_PRIVATE_CHAT_MESSAGE,
    GET_FRIEND_REQUESTS,
    FRIEND_REQUEST_ANSWER,
    ADD_FRIEND,
    REMOVE_FRIEND,
    GET_FRIENDS_LIST,
    GET_OUTGOING_FRIEND

}
