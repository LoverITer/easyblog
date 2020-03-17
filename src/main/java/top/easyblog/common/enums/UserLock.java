package top.easyblog.common.enums;

/**
 *@author huangxin
 */

public enum UserLock {
    /**
     * 不锁定账户
     */
    UNLOCK(0),

    /**
     * 锁定账户
     */
    LOCK(1);

    private int status;

    UserLock(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
