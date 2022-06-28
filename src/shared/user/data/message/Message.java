package shared.user.data.message;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

abstract public class Message implements Serializable {
    protected String writer;
    protected LocalDateTime time;
    private final HashMap<Reacts, HashSet<String>> reacts;
    public Message(String writer) {
        this.writer = writer;
        this.time = LocalDateTime.now();
        this.reacts = new HashMap<>();
        reacts.put(Reacts.LIKE, new HashSet<>());
        reacts.put(Reacts.DISLIKE, new HashSet<>());
        reacts.put(Reacts.LOL, new HashSet<>());
    }
    public void addReaction(Reacts react, String username) {
        reacts.get(react).add(username);
    }
}
