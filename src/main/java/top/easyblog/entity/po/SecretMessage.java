package top.easyblog.entity.po;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huangxin
 */
public class SecretMessage implements Serializable {

    private static final long serialVersionUID = 4752651054600038840L;

    private String messageTopic;

    private String messageContext;

    private Integer sendId;

    private Integer receiveId;

    private Date messageTime;

    public SecretMessage() {
    }

    public SecretMessage(String messageTopic, String messageContext, Integer sendId, Integer receiveId, Date messageTime) {
        this.messageTopic = messageTopic;
        this.messageContext = messageContext;
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.messageTime = messageTime;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public void setMessageTopic(String messageTopic) {
        this.messageTopic = messageTopic == null ? null : messageTopic.trim();
    }

    public String getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(String messageContext) {
        this.messageContext = messageContext == null ? null : messageContext.trim();
    }

    public Integer getSendId() {
        return sendId;
    }

    public void setSendId(Integer sendId) {
        this.sendId = sendId;
    }

    public Integer getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Integer receiveId) {
        this.receiveId = receiveId;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    @Override
    public String toString() {
        return "SecretMessage{" +
                "messageTopic='" + messageTopic + '\'' +
                ", messageContext='" + messageContext + '\'' +
                ", sendId=" + sendId +
                ", receiveId=" + receiveId +
                ", messageTime=" + messageTime +
                '}';
    }
}