package client.requests;

import java.io.Serializable;

public class Request implements Serializable {
    private ReqType type;
    private String description;

    public Request(ReqType type, String description) {
        this.type = type;
        this.description = description;
    }

    public ReqType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
