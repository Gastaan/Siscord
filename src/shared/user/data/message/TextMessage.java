package shared.user.data.message;


import java.util.ArrayList;

public class TextMessage extends Message{
    private final String text;

    //constructor
    public TextMessage(String writer, String text) {
        super(writer);
        this.text = text;

    }
    //toString
    public String toString() {
        return  writer + " " + time+ "\n" + text + "\n" + getReacts();
    }
    public String[] getMentionedUsers() {
        ArrayList<String> mentionedUsers = new ArrayList<>();
        for(String word : text.split(" "))
            if(word.startsWith("@"))
                mentionedUsers.add(word.substring(1));
        return mentionedUsers.toArray(new String[mentionedUsers.size()]);
    }
}
