package server.data;

import shared.user.data.message.Message;

import java.util.Vector;

public class PrivateChat {
    private Vector<Message> messages;
    private boolean isInChat;
    //constructor
    public PrivateChat() {
        messages = new Vector<>();
        isInChat = false;
    }
    //getters
    public Vector<Message> getMessages() {
        return messages;
    }
    public boolean isInChat() {
        return isInChat;
    }
    public void addMessage(Message message) {
        messages.add(message);
    }
    public void changeIsInChat() {
            isInChat = !isInChat;
    }
}
