package org.easyblog.bean;

import java.io.Serializable;
import java.util.Date;

public class UserPhoneLog implements Serializable {

    private static final long serialVersionUID = 7393331110726779645L;
    private Long logId;

    private Integer userId;

    private Date logTime;

    private String context;

    public UserPhoneLog() {
    }

    public UserPhoneLog( Date logTime, String context) {
        this.logTime = logTime;
        this.context = context;
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

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context == null ? null : context.trim();
    }

    @Override
    public String toString() {
        return "UserPhoneLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", logTime=" + logTime +
                ", context='" + context + '\'' +
                '}';
    }
}