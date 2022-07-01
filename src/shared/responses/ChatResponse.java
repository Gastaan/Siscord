package shared.responses;

import shared.user.data.message.Message;

import java.util.ArrayList;

public class ChatResponse extends Response{
    private final ArrayList<Message> messages;
    private final String username;
    //constructor
    public ChatResponse(ArrayList<Message> messages, String username) {
        super(ResType.PRIVATE_CHAT);
        this.messages = messages;
        this.username = username;
    }
    //getters
    public ArrayList<Message> getMessages() {
        return messages;
    }
    public String getUsername() {
        return username;
    }
}
