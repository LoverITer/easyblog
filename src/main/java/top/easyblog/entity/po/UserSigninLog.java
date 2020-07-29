package top.easyblog.entity.po;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;


/**
 * @author huangxin
 */
public class UserSigninLog implements Serializable {

    private static final long serialVersionUID = 8995845661621294868L;
    @Id
    private Long logId;

    private Integer userId;

    private String loginIp;

    private String loginLocation;

    private Date loginTime;

    private String loginResult;

    public UserSigninLog() {
    }

    public UserSigninLog(Integer userId, String loginIp, String loginLocation, String loginResult) {
        this.userId = userId;
        this.loginIp = loginIp;
        this.loginLocation = loginLocation;
        this.loginResult = loginResult;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp == null ? null : loginIp.trim();
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation == null ? null : loginLocation.trim();
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(String loginResult) {
        this.loginResult = loginResult;
    }

    @Override
    public String toString() {
        return "UserSignInLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", loginIp='" + loginIp + '\'' +
                ", loginLocation='" + loginLocation + '\'' +
                ", loginTime=" + loginTime +
                '}';
    }
}