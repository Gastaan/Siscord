package shared.requests;

import java.io.Serializable;

 public class Request implements Serializable {
    protected RequestType type;


    public Request(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }
}
