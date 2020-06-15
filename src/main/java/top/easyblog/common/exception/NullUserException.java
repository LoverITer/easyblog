package top.easyblog.common.exception;


/**
 * @author huangxin
 */
public class NullUserException extends RuntimeException {

    private static final long serialVersionUID = -3264035014084830809L;

    public NullUserException() {
    }

    public NullUserException(String message) {
        super(message);
    }

    public NullUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
