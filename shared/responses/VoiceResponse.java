package shared.responses;

public class VoiceResponse extends Response{
    private final byte[] bufferedVoice;
    private final String username;
    private final int bytesRead;
    //constructor
    public VoiceResponse(byte[] bufferedVoice,int bytesRead, String username) {
        super(ResponseType.VOICE);
        this.bufferedVoice = bufferedVoice;
        this.bytesRead = bytesRead;
        this.username = username;
    }
    //getters
    public byte[] getBufferedVoice() {
        return bufferedVoice;
    }
    public String getUsername() {
        return username;
    }
    public int getBytesRead() {
        return bytesRead;
    }
}
