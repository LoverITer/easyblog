package top.easyblog.entity.po;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;


/**
 * @author huangxin
 */
public class UserMailLog implements Serializable {

    private static final long serialVersionUID = -8994188567591744802L;
    @Id
    private Long logId;

    private String email;

    private Date logTime;

    private String context;

    public UserMailLog() {
    }

    public UserMailLog(String email,  String context) {
        this.email=email;
        this.context = context;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getUserId() {
        return email;
    }

    public void setUserId(String email) {
        this.email = email;
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
        return "UserMailLog{" +
                "logId=" + logId +
                ", email=" + email +
                ", logTime=" + logTime +
                ", context='" + context + '\'' +
                '}';
    }
}