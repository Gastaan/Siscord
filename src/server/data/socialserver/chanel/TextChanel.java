package server.data.socialserver.chanel;

import server.data.Chat;

public class TextChanel extends Chanel {
    private final Chat chat;
    //constructor
    public TextChanel(Chat chat) {
        this.chat = chat;
    }
    //getters
    public Chat getChat() {
        return chat;
    }
}
