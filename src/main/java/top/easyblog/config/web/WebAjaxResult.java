package top.easyblog.config.web;


import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 向页面返回JSON格式信息
 * @author huangxin
 */
public class WebAjaxResult implements Serializable {

    private static final long serialVersionUID = 1987961797257523721L;
    private String message="";
    private boolean success = false;
    private Map<String, Object> model = null;

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

    public void setModel(String k, Object v) {
        if (model == null) {
            synchronized (WebAjaxResult.class) {
                if (model == null) {
                    model = new ConcurrentHashMap<>(16);
                }
            }
        }
        this.model.put(k, v);
    }

    public Map<String, Object> getModel() {
        return this.model;
    }

}
