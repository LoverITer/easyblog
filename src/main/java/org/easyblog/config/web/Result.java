package org.easyblog.config.web;


/**
 * 向页面返回信息
 */
public class Result {

    private String msg;

    private boolean success = false;

    public String getMsg() {
        return msg;
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
