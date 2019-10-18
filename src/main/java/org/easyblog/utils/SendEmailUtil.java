package org.easyblog.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class SendEmailUtil {

    private static final Logger log= LoggerFactory.getLogger(SendEmailUtil.class);

    @Autowired
    JavaMailSenderImpl mailSender;

    public boolean send(Email email) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setSubject(email.getSubject());
            simpleMailMessage.setFrom(email.sendForm);
            simpleMailMessage.setTo(email.sendTo);
            simpleMailMessage.setText(email.sendText);
            mailSender.send(simpleMailMessage);
        }catch (Exception e){
            log.error("发送邮件失败"+e.getMessage());
            return false;
        }
        return true;
    }

    public static class Email {

        String subject;
        String sendForm ="2489868503@qq.com";
        String sendTo;
        String sendText;
        String attachment;

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


}
