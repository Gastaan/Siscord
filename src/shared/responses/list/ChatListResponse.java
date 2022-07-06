package shared.responses.list;

import shared.responses.ResType;
import shared.responses.Response;

import java.util.ArrayList;

public class ChatListResponse extends Response {
        private final ArrayList<String> chatNames;
        //constructor
        public ChatListResponse( ArrayList<String> chatNames) {
            super(ResType.LIST);
            this.chatNames = chatNames;
        }
        public ArrayList<String> getChatNames() {
            return chatNames;
        }

}
