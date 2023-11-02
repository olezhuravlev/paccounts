package org.paccounts.exception;

public class IncompatibleParametersException extends RuntimeException {

    /**
     * Serialization version.
     */
    @java.io.Serial
    private static final long serialVersionUID = -1633418712876262839L;

    public IncompatibleParametersException(String message) {
        super(message);
    }

    public IncompatibleParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
