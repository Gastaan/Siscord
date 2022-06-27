package shared.responses;

import java.io.Serializable;

 public class Response implements Serializable {
    private ResType resType;

    public Response(ResType resType) {
        this.resType = resType;
    }

    public ResType getResType() {
        return resType;
    }

}
