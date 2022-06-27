package shared.user.data.message;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

abstract public class Message implements Serializable {
    protected String writer;
    protected LocalDateTime time;
    private final HashMap<Reacts, Integer> reacts;
    public Message(String writer) {
        this.writer = writer;
        this.time = LocalDateTime.now();
        this.reacts = new HashMap<>();
        reacts.put(Reacts.LIKE, 0);
        reacts.put(Reacts.DISLIKE, 0);
        reacts.put(Reacts.LOL, 0);
    }
}
