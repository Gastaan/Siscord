package server.data.socialserver.chanel;

import java.util.HashSet;

abstract public class Chanel {
    private boolean isLimited;
    private final HashSet<String> accessList;
    //constructor
    public Chanel() {
        isLimited = false;
        accessList = new HashSet<>();
    }
   public boolean getIsLimited() {

        return isLimited;
    }
    public void setIsLimited(boolean isLimited) {
        this.isLimited = isLimited;
    }
    public boolean hasAccess(String username) {
        if(isLimited) {
            synchronized (accessList) {
                return accessList.contains(username);
            }
        }
        else return true;
    }
    public void addAccess(String username) {
        accessList.add(username);
    }
    public HashSet<String> getAccessList() {
        synchronized (accessList) {
            return new HashSet<>(accessList);
        }
    }
}
