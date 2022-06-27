package shared.requests;

import java.io.Serializable;

abstract public class Request implements Serializable {
    protected ReqType type;


    public Request(ReqType type) {
        this.type = type;
    }

    public ReqType getType() {
        return type;
    }
}