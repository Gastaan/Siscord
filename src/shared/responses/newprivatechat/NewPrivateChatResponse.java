package shared.responses.newprivatechat;

import shared.responses.ResType;
import shared.responses.Response;

public class NewPrivateChatResponse extends Response {
    private final NewPrivateChatStatus status;
    //constructor
    public NewPrivateChatResponse(NewPrivateChatStatus status) {
        super(ResType.NEW_PRIVATE_CHAT);
        this.status = status;
    }
    //toString
    @Override
    public String toString() {
        return "NewPrivateChatResponse{" +
                "status=" + status +
                '}';
    }
}
