package top.easyblog.common.enums;


/**
 * @author huangxin
 */
public enum UserLoginStatus {

    /**
     * 登录的用户
     */
    LOGIN(1),

    /**
     * 未登录
     */
    UNLOGIN(0);


    private int status;

    UserLoginStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
