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
    DELETE_CHANEL,
    ADD_FRIEND_TO_SERVER,
    SERVER_MEMBERS,
    KICK_MEMBER,
    BLOCK_MEMBER,
    GIVE_ROLE,
    STATUS_CHANGED,
    LOGGED_OUT
}
