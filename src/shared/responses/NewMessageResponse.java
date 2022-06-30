package shared.responses;

import shared.user.data.message.Message;

public class NewMessageResponse extends Response {
    private final Message newMessage;
    //constructor
    public NewMessageResponse(Message newMessage) {
        super(ResType.NEW_MESSAGE);
        this.newMessage = newMessage;
    }
    //getters
    public Message getNewMessage() {
        return newMessage;
    }
}
