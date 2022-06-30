package shared.requests;

public class GetFriendsListRequest extends Request {
    private final String username;
    //constructor
    public GetFriendsListRequest(String username) {
        super(ReqType.GET_FRIENDS_LIST);
        this.username = username;
    }
    //getters
    public String getUsername() {
        return username;
    }
}
