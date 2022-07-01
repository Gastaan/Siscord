package shared.requests;

import shared.user.data.message.Message;

public class NewPrivateChatMessageRequest extends  Request{
    private final Message message;
    private final String receiver;
    //constructor
    public NewPrivateChatMessageRequest(Message message, String receiver) {
        super(ReqType.NEW_PRIVATE_CHAT_MESSAGE);
        this.message = message;
        this.receiver = receiver;
    }
    //getters
    public Message getMessage() {
        return message;
    }
    public String getReceiver() {
        return receiver;
    }
}
