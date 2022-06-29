package server.data;

import server.ClientHandler;
import shared.user.data.message.Message;

import java.util.HashSet;
import java.util.Vector;

public class PrivateChat {
    //fields
    private final Vector<Message> messages;
    private final HashSet<ClientHandler> inChat;
    //constructor
    public PrivateChat() {
        messages = new Vector<>();
        inChat = new HashSet<>();
    }
    //methods
    public Vector<Message> getMessages() {
        return messages;
    }
    public void addMessage(Message message) {
        messages.add(message);
    }
    public void addInChat(ClientHandler clientHandler) {
        inChat.add(clientHandler);
    }
    public void removeInChat(ClientHandler clientHandler) {
        inChat.remove(clientHandler);
    }
}
