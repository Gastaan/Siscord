package user.data.message;

import java.io.Serializable;
import java.time.LocalDateTime;

abstract public class Message implements Serializable {
    protected String writer;
    protected LocalDateTime time;
    public Message(String writer) {
        this.writer = writer;
        this.time = LocalDateTime.now();
    }
}
