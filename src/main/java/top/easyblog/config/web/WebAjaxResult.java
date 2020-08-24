package top.easyblog.config.web;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 向页面返回JSON格式信息
 * @author huangxin
 */
public class WebAjaxResult implements Serializable {

    private static final long serialVersionUID = 1987961797257523721L;
    private String message="";
    private boolean success = false;
    private final Map<String, Object> model = new HashMap<>();

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

    public WebAjaxResult setModel(String k, Object v) {
        this.model.put(k, v);
        return this;
    }

    public Map<String, Object> getModel() {
        return this.model;
    }

}
