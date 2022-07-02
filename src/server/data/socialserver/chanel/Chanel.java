package server.data.socialserver.chanel;

import java.util.HashSet;

abstract public class Chanel {
    private boolean isLimited;
    private HashSet<String> accessList;
    //constructor
    public Chanel() {
        isLimited = false;
        accessList = new HashSet<>();
    }
    public void setLimited(boolean isLimited) {
        this.isLimited = isLimited;
    }
    public void addAccess(String username) {
        accessList.add(username);
    }
}
