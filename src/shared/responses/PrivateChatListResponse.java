package shared.responses;

import java.util.ArrayList;

public class PrivateChatListResponse extends  Response {
        private final ArrayList<String> chatNames;
        //constructor
        public PrivateChatListResponse(ArrayList<String> chatNames) {
            super(ResType.PRIVATE_CHAT_LIST);
            this.chatNames = chatNames;
        }
        //getters
        public Integer getCount() {
            return chatNames.size();
        }
        public ArrayList<String> getChatNames() {
            return chatNames;
        }
        public void addChatName(String chatName) {
            chatNames.add(chatName);
        }
}
