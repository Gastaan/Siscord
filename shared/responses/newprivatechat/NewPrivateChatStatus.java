package shared.responses.newprivatechat;

import java.io.Serializable;

public enum NewPrivateChatStatus implements Serializable {
    USER_NOT_FOUND,
    ALREADY_CHAT_EXISTS,
    CHAT_CREATED
}
