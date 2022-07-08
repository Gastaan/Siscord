package shared.responses;

public class Notification extends Response{
    private final String description;
    //constructor
    public Notification(String description) {
        super(ResponseType.NOTIFICATION);
        this.description = description;
    }
    //getters
    public String getDescription() {
        return description;
    }
}
