package top.easyblog.common.exception;

/**
 * 分页参数异常
 * @author huangxin
 */
public class IllegalPageParameterException extends IllegalArgumentException{

    private static final long serialVersionUID = -8992282390988663318L;

    public IllegalPageParameterException() {
        super("分页参数异常");
    }

    public IllegalPageParameterException(String s) {
        super(s);
    }

    public IllegalPageParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPageParameterException(Throwable cause) {
        super(cause);
    }
}
