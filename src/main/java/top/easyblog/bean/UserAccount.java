package top.easyblog.bean;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author HuangXin
 * @since 2020/2/4 22:56
 * 用户的其他账号
 */
public class UserAccount implements Serializable {


    private static final long serialVersionUID = -5577288584992137710L;
    @Id
    private Integer accountId;   //id
    private String github;     //github
    private String wechat;    //微信
    private String qq;         //qq
    private String steam;      //steam
    private String twitter;    //twitter
    private String weibo;       //微博
    private Integer accountUser;         //用户id

    public UserAccount() {
    }

    public UserAccount(String github, String wechat, String qq, String steam, String twitter, String weibo, Integer accountUser) {
        this.github = github;
        this.wechat = wechat;
        this.qq = qq;
        this.steam = steam;
        this.twitter = twitter;
        this.weibo = weibo;
        this.accountUser = accountUser;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(Integer accountUser) {
        this.accountUser = accountUser;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getSteam() {
        return steam;
    }

    public void setSteam(String steam) {
        this.steam = steam;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "accountId=" + accountId +
                ", github='" + github + '\'' +
                ", wechat='" + wechat + '\'' +
                ", qq='" + qq + '\'' +
                ", steam='" + steam + '\'' +
                ", twitter='" + twitter + '\'' +
                ", weibo='" + weibo + '\'' +
                ", accountUser=" + accountUser +
                '}';
    }
}
