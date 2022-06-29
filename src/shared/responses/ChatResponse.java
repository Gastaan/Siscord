package shared.responses;

import shared.user.data.message.Message;

import java.util.Vector;

public class ChatResponse extends Response{
    private final Vector<Message> messages;
    //constructor
    public ChatResponse(Vector<Message> messages) {
        super(ResType.PRIVATE_CHAT);
        this.messages = messages;
    }
    //getters
    public Vector<Message> getMessages() {
        return messages;
    }
    public void addMessage(Message message) {
        messages.add(message);
    }
}
