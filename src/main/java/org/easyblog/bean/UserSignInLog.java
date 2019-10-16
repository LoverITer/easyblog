package org.easyblog.bean;

import java.io.Serializable;
import java.util.Date;

public class UserSignInLog implements Serializable {
    private Long logId;

    private Integer userId;

    private String loginIp;

    private String loginLocation;

    private Date loginTime;

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