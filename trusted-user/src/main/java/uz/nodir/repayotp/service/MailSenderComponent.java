package uz.nodir.repayotp.service;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;



public class MailSenderComponent {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public void sendEmail(String to, String code) {
        log.info("Sending email with OTP to sender: {}", to);

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "127.0.0.1");
            props.put("mail.smtp.port", "25");


            Session session = Session.getInstance(props);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("user@example.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Your OTP Code");

            var text = "Ваш код: %s для подтверждения входа в систему REPAY.".formatted(code);
            message.setText(text);
            Transport.send(message);

            log.info("Email sent successfully");
        } catch (Exception e) {
            log.error("Occurred error while sending email with message: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
