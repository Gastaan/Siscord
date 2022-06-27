package user.data.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileMessageTest {
    @Test
    void download() {
        FileMessage fileMessage = new FileMessage("gastan", new byte[]{1, 2, 3}, "test.bin");
        fileMessage.download();
    }

}