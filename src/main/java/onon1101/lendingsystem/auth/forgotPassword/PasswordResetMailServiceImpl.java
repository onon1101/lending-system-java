package onon1101.lendingsystem.auth.forgotPassword;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetMailServiceImpl implements PasswordResetMailService {
    private final JavaMailSender mailSender;
    private final String senderEmail;
    private final String resetPasswordUrl;

    public PasswordResetMailServiceImpl(
            JavaMailSender mailSender,
            @Value("${app.mail.sender}") String senderEmail,
            @Value("${app.reset-password-url}") String resetPasswordUrl) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    @Override
    public void sendResetPasswordEmail(String recipientEmail, String username, String resetToken) {
        String link = resetPasswordUrl + "?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        // todo: 放到設定檔案當中
        message.setSubject("重設密碼");
        message.setText(
                """
                您好 %s：

                我們收到您的密碼重設申請，請點擊以下連結設定新密碼：

                %s

                此連結將在 15 分鐘後失效。
                如果這不是您提出的申請，請忽略此郵件。
                """
                        .formatted(username, link));

        mailSender.send(message);
    }
}
