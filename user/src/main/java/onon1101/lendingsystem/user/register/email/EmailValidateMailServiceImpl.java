package onon1101.lendingsystem.user.register.email;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailValidateMailServiceImpl implements EmailValidateMailService {
    private final JavaMailSender mailSender;
    private final String senderEmail;
    private final String emailValidationUrl;

    public EmailValidateMailServiceImpl(
            JavaMailSender mailSender,
            @Value("${app.mail.sender}") String senderEmail,
            @Value("${app.email-validation-url}") String emailValidationUrl) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
        this.emailValidationUrl = emailValidationUrl;
    }

    @Override
    public void sendEmailValidateEmail(
            String recipientEmail, String username, String validateEmailToken) {
        String separator = emailValidationUrl.contains("?") ? "&" : "?";
        String link =
                emailValidationUrl
                        + separator
                        + "token="
                        + URLEncoder.encode(validateEmailToken, StandardCharsets.UTF_8);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("驗證您的 Email");
        message.setText(
                """
                您好 %s：

                感謝您註冊，請點擊以下連結完成 Email 驗證：

                %s

                此連結將在 24 小時後失效。
                如果這不是您的操作，請忽略此郵件。
                """
                        .formatted(username, link));
        mailSender.send(message);
    }
}
