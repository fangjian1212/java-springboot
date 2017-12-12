/*
 */
package com.fangjian.share.session;

/**
 */
public class SessionException extends RuntimeException {
    private static final long serialVersionUID = 8509327411808483929L;

    public SessionException() {
        super();
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }

}
