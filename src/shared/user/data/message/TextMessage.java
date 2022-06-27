package shared.user.data.message;


public class TextMessage extends Message{
    private final String text;

    //constructor
    public TextMessage(String writer, String text) {
        super(writer);
        this.text = text;

    }
    //toString
    public String toString() {
        return  writer + " " + time+ "\n" + text + "\n";
    }
}