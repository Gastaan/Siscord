package shared.responses;

import shared.user.data.message.Message;

import java.util.ArrayList;

/**
 * @author saman hazemi
 * This class is used to send a list of messages to the client.
 * It is used in the following cases:
 * 1- When the client requests the list of messages in a private chat.
 * 2- When the client requests the list of messages in a text channel.
 */
public class ChatResponse extends Response{
    private final ArrayList<Message> messages;
    private final ArrayList<Message> pinnedMessages;
    private final String[] placeholder;

    /**
     * @param messages the list of messages to be sent to the client.
     * @param pinnedMessages the list of pinned messages to be sent to the client.
     * @param placeholder the list of placeholder to be sent to the client.
     */
    //constructor
    public ChatResponse(ArrayList<Message> messages, ArrayList<Message> pinnedMessages,  String[] placeholder) {
        super(ResponseType.CHAT);
        this.messages = messages;
        this.pinnedMessages = pinnedMessages;
        this.placeholder = placeholder;
    }
    //getters

    /**
     * @return the list of messages to be sent to the client.
     */
    public ArrayList<Message> getMessages() {
        return messages;
    }

    /**
     * @return the list of pinned messages to be sent to the client.
     */
    public ArrayList<Message> getPinnedMessages() {
        return pinnedMessages;
    }

    /**
     * @return the list of placeholder to be sent to the client.
     */
    public String[] getPlaceholder() {
        return placeholder;
    }

    /**
     * This method prints all the messages in the chat.
     */
    public void printAllMessages() {
        int index = 1;
        System.out.println("messages: ");
        for (Message message : messages)
            System.out.println(index++ + "- " + message);
    }
    /**
     * This method prints all pinned messages in the chat.
     */
    public void printPinnedMessages() {
        System.out.println("Pinned messages:");
        for (Message message : pinnedMessages) {
            System.out.println(message);
        }
    }
}
