package shared.responses;

import java.util.ArrayList;

public class PrivateChatListResponse extends  Response {
        private final Integer count;
        private final ArrayList<String> chatNames;
        //constructor
        public PrivateChatListResponse(ArrayList<String> chatNames) {
            super(ResType.PRIVATE_CHAT_LIST);
            count = chatNames.size();
            this.chatNames = chatNames;
        }
        //getters
        public Integer getCount() {
            return count;
        }
        public ArrayList<String> getChatNames() {
            return chatNames;
        }
}
