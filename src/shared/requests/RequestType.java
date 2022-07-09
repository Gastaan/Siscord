package shared.requests;

import java.io.Serializable;

/**
 * This class is used to represent request types.
 */
public enum RequestType implements Serializable {
    LOGIN,
    SIGN_UP,
    PRIVATE_CHAT_LIST,
    CHAT_REQUEST,
    CHAT_REACT,
    IS_TYPING,
    NEW_PRIVATE_CHAT,
    NEW_MESSAGE,
    GET_INCOMING_FRIEND_REQUESTS,
    FRIEND_REQUEST_ANSWER,
    ADD_FRIEND,
    REMOVE_FRIEND,
    GET_FRIENDS_LIST,
    GET_OUTGOING_FRIEND,
    GET_BLOCKED_USERS,
    UNBLOCK_USER,
    BLOCK_USER,
    NEW_SERVER,
    SERVER_LIST,
    SERVER_CHANELS,
    CHANGE_PASSWORD,
    PIN_MESSAGE,
    CANCEL_FRIEND_REQUEST,
    CHANGE_EMAIL,
    CHANGE_PHONE_NUMBER,
    CREATE_CHANEL,
    DELETE_CHANEL,
    ADD_FRIEND_TO_SERVER,
    SERVER_MEMBERS,
    KICK_MEMBER
}
