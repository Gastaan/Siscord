package server.responses;

import java.io.Serializable;

 public class Response implements Serializable {
    private ResType resType;
    private String description;
    private Object responded;

    public Response(ResType resType, String description, Object respond) {
        this.resType = resType;
        this.description = description;
        this.responded = respond;
    }

    public ResType getResType() {
        return resType;
    }

    public String getDescription() {
        return description;
    }

    public Object getResponded() {
        return responded;
    }
}
