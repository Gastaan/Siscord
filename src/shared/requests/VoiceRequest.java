package shared.requests;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class VoiceRequest extends Request{
    private final byte[] bufferedVoice;
    private final String username;
    //constructor
    public VoiceRequest(byte[] bufferedVoice, String username) {
        super(RequestType.VOICE);
        this.bufferedVoice = bufferedVoice;
        this.username = username;
    }
    //getters
    //This method plays the voice message by source data line.
    public void playVoice() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        SourceDataLine speaker;
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(format);
            speaker.start();
            speaker.write(bufferedVoice, 0, bufferedVoice.length);
            speaker.drain();
            speaker.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getUsername() {
        return username;
    }
}
