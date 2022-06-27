package user.data.message;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileMessage extends Message{
    private final byte[] file;
    private String filename;
    //constructor
    public FileMessage(String writer, byte[] file, String filename) {
        super(writer);
        this.file = file;
        this.filename = filename;

    }
    //toString
    public String toString() {
        return  writer + " " + time+ "\n" + filename + "\n";
    }
    //download file
    public void download() {
    try {
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(file);
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
