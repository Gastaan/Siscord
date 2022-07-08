package shared.responses;

import java.io.Serializable;

/**
 * @author saman hazemi
 */
 public class Response implements Serializable {
    private ResponseType responseType;

    public Response(ResponseType responseType) {
        this.responseType = responseType;
    }

    public ResponseType getResType() {
        return responseType;
    }

}
