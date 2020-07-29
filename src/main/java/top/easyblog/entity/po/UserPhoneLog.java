package top.easyblog.entity.po;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huangxin
 */
public class UserPhoneLog implements Serializable {

    private static final long serialVersionUID = 7393331110726779645L;
    @Id
    private Long logId;

    private String phone;

    private Date logTime;

    private String context;

    public UserPhoneLog() {
    }

    public UserPhoneLog(String phone, String context) {
        this.phone = phone;
        this.context = context;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getUserId() {
        return phone;
    }

    public void setUserId(String userId) {
        this.phone = phone;
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
                ", phone=" + phone +
                ", logTime=" + logTime +
                ", context='" + context + '\'' +
                '}';
    }
}