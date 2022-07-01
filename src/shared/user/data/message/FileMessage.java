package shared.user.data.message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMessage extends Message{
    private final byte[] file;
    private final String filename;
    //constructor
    public FileMessage(String writer, byte[] file, String filename) {
        super(writer);
        this.file = file;
        this.filename = filename;

    }
    //download file
    public void download() { //TODO : OPTIMIZE
    try {
        Path path = Paths.get("./clients/downloads/"+ writer);
        if(!Files.exists(path))
            Files.createDirectories(path);
        path = Paths.get("./clients/downloads/"+ writer + "/" + filename);
        int i = 1;
        while(Files.exists(path)) {
            String newName = filename.substring(0, filename.lastIndexOf('.')) + "(" + i + ")" + filename.substring(filename.lastIndexOf('.'));
            path = Paths.get("./clients/downloads/" +  writer + "/" + newName);
            i++;
        }
            FileOutputStream fos = new FileOutputStream(path.toString());
            fos.write(file);
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    //toString
    @Override
    public String toString() {
        return  writer + " " + time+ "\n" + filename + "\n" + getReacts();
    }
}
