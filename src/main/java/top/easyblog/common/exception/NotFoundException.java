package top.easyblog.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author huangxin
 */
@ResponseStatus(code= HttpStatus.NOT_FOUND,reason = "你访问的博客找不到啦~！")
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3546759950204192931L;

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
