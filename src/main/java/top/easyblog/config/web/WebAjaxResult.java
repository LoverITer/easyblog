package top.easyblog.config.web;


import java.io.Serializable;

/**
 * 向页面返回JSON格式信息
 * @author huangxin
 */
public class WebAjaxResult implements Serializable {

    private static final long serialVersionUID = 1987961797257523721L;
    private String message="";
    private boolean success = false;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
