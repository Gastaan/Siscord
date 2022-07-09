package shared.responses;

import java.io.Serializable;

/**
 * @author saman hazemi
 * The response type enum.
 */
public enum ResponseType implements Serializable {
    LOGIN,
    SIGNUP,
    LIST,
    CHAT,
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
    CHANGE_PASSWORD,
    PIN_MESSAGE,
    MESSAGE_DELIVERED,
    REACTED_TO_MESSAGE,
    CHANGE_EMAIL,
    CHANGE_PHONE_NUMBER,
    PERMISSION_DENIED,
    CREATE_CHANEL,
}
