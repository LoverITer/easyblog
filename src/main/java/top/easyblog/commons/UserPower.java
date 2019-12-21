package top.easyblog.commons;


/**
 * 用户权限
 */
public enum UserPower {

    /**
     * 管理员
     */
    ADMIN(1),

    /**
     * VIP用户
     */
    VIP_USER(2),

    /**
     * 普通用户
     */
    USER(3);

    private int level;

    UserPower(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    @Override
    public String toString() {
        return "Power{" +
                "level=" + level +
                '}';
    }
}