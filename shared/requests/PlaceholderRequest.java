package shared.requests;

public class PlaceholderRequest extends Request {
    private final String[] placeholder;
    //constructor
    public PlaceholderRequest(RequestType type, String... placeholder) {
        super(type);
        this.placeholder = placeholder;
    }
    //getters
    public String[] getPlaceholder() {
        return placeholder;
    }
}
