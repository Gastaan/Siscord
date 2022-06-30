package shared.responses.signup;

import java.io.Serializable;

public enum SignUpStatus implements Serializable {
    VALID,
    INVALID,
    DUPLICATE
}
