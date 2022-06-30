package shared.responses;

import java.util.ArrayList;

public class GetFriendsListResponse extends Response {
    private final ArrayList<String> friends;
    //constructor
    public GetFriendsListResponse(ArrayList<String> friends) {
        super(ResType.GET_FRIENDS_LIST);
        this.friends = friends;
    }
    //getters
    public ArrayList<String> getFriends() {
        return friends;
    }
}
