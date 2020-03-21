package top.easyblog.common.enums;

/**
 * 用户账号的状态
 * @author huangxin
 */
public enum UserFreeze {


    /**
     * 不冻结账户
     */
    UNFREEZE(0),

    /**
     * 冻结账户
     */
    FREEZE(1);



    private int status;

    UserFreeze(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
