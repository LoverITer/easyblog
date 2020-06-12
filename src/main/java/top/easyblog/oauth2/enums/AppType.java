package top.easyblog.oauth2.enums;

/**
 * 第三方授权认证的APPType
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/29 10:58
 */
public enum AppType {

    /**
     * QQ
     */
    QQ("qq"),

    /**
     * 微信
     */
    WeChat("WeChat"),

    /**
     * GitHub
     */
    GitHub("GitHub");


    private String appType;

    AppType(String appType) {
        this.appType = appType;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
}
