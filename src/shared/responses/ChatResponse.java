package shared.responses;

import shared.user.data.message.Message;

import java.util.ArrayList;

public class ChatResponse extends Response{
    private final ArrayList<Message> messages;
    public ChatResponse(ArrayList<Message> messages) {
        super(ResType.PRIVATE_CHAT);
        this.messages = messages;
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }
}
