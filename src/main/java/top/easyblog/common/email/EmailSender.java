package top.easyblog.common.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

/**
 * @author huangxin
 */
@Slf4j
@Component
public class EmailSender {


    private final JavaMailSender mailSender;

    public EmailSender(JavaMailSenderImpl mailSender) {
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
            return true;
        }catch (Exception e){
            log.error("发送邮件失败,Exception info:"+e.getMessage());
        }
        return false;
    }

}
