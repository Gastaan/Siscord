package server.data;

import shared.user.data.message.Message;
import shared.user.data.message.Reacts;

import java.util.ArrayList;
import java.util.Vector;

public class Chat {
    //fields
    private final Vector<Message> messages;
    //constructor
    public Chat() {
        messages = new Vector<>();
    }
    //methods
    public ArrayList<Message> getMessages() {
        return new ArrayList<>(messages);
    }
    public void addMessage(Message message) {
        messages.add(message);
    }
    public void addReaction(String username, String message, Reacts react) {
        synchronized (messages) {
            for (Message checkingMessage : messages) {
                if (checkingMessage.getTime().equals(message)) {
                    checkingMessage.addReaction(react, username);
                    break;
                }
            }
        }
    }
}
