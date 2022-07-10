package shared.requests;

public class Voice extends Request{
    private final byte[] bufferedVoice;
    int bytesRead;
    private final String username;
    //constructor
    public Voice(byte[] bufferedVoice,int bytesRead, String username) {
        super(RequestType.VOICE);
        this.bufferedVoice = bufferedVoice;
        this.bytesRead = bytesRead;
        this.username = username;
    }
    //getters
    public byte[] getBufferedVoice() {
        return bufferedVoice;
    }
    public int getBytesRead() {
        return bytesRead;
    }
    public String getUsername() {
        return username;
    }
}
