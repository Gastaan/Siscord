package server.responses;

import java.io.Serializable;

 public class Response <T extends Serializable> implements Serializable {
    private ResType resType;
    private String description;
    private T responded;

    public Response(ResType resType, String description, T respond) {
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

    public T getResponded() {
        return responded;
    }
}
