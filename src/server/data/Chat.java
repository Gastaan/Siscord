package server.data;

import shared.user.data.message.Message;
import shared.user.data.message.Reacts;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author saman hazemi
 * This class is chat data.
 * It is used to store the messages in a chat.
 * It is used in the following cases:
 * 1- When the client requests the list of messages in a private chat.
 * 2- When the client requests the list of messages in a text channel.
 */
public class Chat {
    //fields
    private final Vector<Message> messages;
    private final Vector<Message> pinnedMessages;
    //constructor

    /**
     * constructor of the chat class.
     */
    public Chat() {
        messages = new Vector<>();
        pinnedMessages = new Vector<>();
    }
    //methods
    /**
     * @return the list of messages in the chat.
     */
    public ArrayList<Message> getMessages() {
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }
    /**
     * @return the list of pinned messages in the chat.
     */
    public ArrayList<Message> getPinnedMessages() {
        synchronized (pinnedMessages) {
            return new ArrayList<>(pinnedMessages);
        }
    }
    /**
     * @param message the message to be added to the chat.
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * @param message the message to be added to the pinned messages.
     */
    public void addPinnedMessage(Message message) {
        pinnedMessages.add(message);
    }

    /**
     * @param username The username of reactor.
     * @param messageTime The messageTime that the reactor reacted to.
     * @param react The React that the reactor added to the messageTime.
     */
    public void addReaction(String username, String messageTime, Reacts react) {
        findMessageByTime(messageTime).addReaction(react, username);
    }

    /**
     * @param messageTime The messageTime of pinned message.
     */
    public void pinMessage(String messageTime) {
        Message message = findMessageByTime(messageTime);
        message.setPinned();
        pinnedMessages.add(message);
    }

    /**
     * @param time The time of the message.
     * @return The message with the given time.
     */
    private Message findMessageByTime(String time) {
        synchronized (messages) {
            for (Message checkingMessage : messages) {
                if (checkingMessage.getTime().equals(time)) {
                    return checkingMessage;
                }
            }
        }
        return null;
    }
}
