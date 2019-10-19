package org.easyblog.handler.exception;


public class NullUserException extends RuntimeException {

    public NullUserException() {
    }

    public NullUserException(String message) {
        super(message);
    }

    public NullUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
