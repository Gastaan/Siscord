package shared.requests;

import java.io.Serializable;

public enum ReqType implements Serializable {
    LOGIN,
    SIGN_UP,
    PRIVATE_CHAT,
    PRIVATE_CHAT_LIST,
    CHAT_REQUEST,
    REACT
}
