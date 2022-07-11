package shared.requests;

import shared.user.data.message.Message;

/**
 * @author saman hazemi
 */
public class NewMessageRequest extends  Request{
    private final Message message;
    private final String[] placeholder;

    /**
     * constructor of the request class.
     * @param message the message to be sent.
     * @param placeholder the place that the message should be sent.
     */
    //constructor
    public NewMessageRequest(Message message, String... placeholder) {
        super(RequestType.NEW_MESSAGE);
        this.message = message;
        this.placeholder = placeholder;
    }
    //getters

    /**
     * @return the message.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @return the placeholder.
     */
    public String[] getPlaceholder() {
        return placeholder;
    }
}
