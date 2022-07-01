package shared.responses;

import java.util.ArrayList;

public class PrivateChatListResponse extends  Response {
        private final ArrayList<String> chatNames;
        //constructor
        public PrivateChatListResponse(ArrayList<String> chatNames) {
            super(ResType.PRIVATE_CHAT_LIST);
            this.chatNames = chatNames;
        }
        public ArrayList<String> getChatNames() {
            return chatNames;
        }
}
