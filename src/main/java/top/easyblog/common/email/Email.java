package top.easyblog.common.email;

/**
 * @author huangxin
 */
public class Email {

    private String subject;
    private String sendForm = "2489868503@qq.com";
    private String sendTo;
    private String sendText;
    private String attachment;

    public Email() {
    }

    public Email(String subject, String sendTo, String sendText, String attachment) {
        this.subject = subject;
        this.sendTo = sendTo;
        this.sendText = sendText;
        this.attachment = attachment;
    }

    public Email(String subject, String sendForm, String sendTo, String sendText, String attachment) {
        this.subject = subject;
        this.sendForm = sendForm;
        this.sendTo = sendTo;
        this.sendText = sendText;
        this.attachment = attachment;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSendForm() {
        return sendForm;
    }

    public void setSendForm(String sendForm) {
        this.sendForm = sendForm;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getSendText() {
        return sendText;
    }

    public void setSendText(String sendText) {
        this.sendText = sendText;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}

