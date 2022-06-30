package shared.responses;

import java.util.ArrayList;

public class GetBlockedUsersResponse extends Response{
    private final ArrayList<String> blockedUsers;
    //constructor
    public GetBlockedUsersResponse(ArrayList<String> blockedUsers) {
        super(ResType.GET_BLOCKED_USERS);
        this.blockedUsers = blockedUsers;
    }
    //getters
    public ArrayList<String> getBlockedUsers() {
        return blockedUsers;
    }
}
