package top.easyblog.commons.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class SendEmailUtil {

    private static final Logger log= LoggerFactory.getLogger(SendEmailUtil.class);

    private final JavaMailSenderImpl mailSender;

    public SendEmailUtil(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public boolean send(Email email) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setSubject(email.getSubject());
            simpleMailMessage.setFrom(email.getSendForm());
            simpleMailMessage.setTo(email.getSendTo());
            simpleMailMessage.setText(email.getSendText());
            mailSender.send(simpleMailMessage);
        }catch (Exception e){
            log.error("发送邮件失败,Exception info:"+e.getMessage());
            return false;
        }
        return true;
    }

}
