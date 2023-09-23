package kz.btsd.edmarket.user.service;

import kz.btsd.edmarket.common.ExceptionLog;
import kz.btsd.edmarket.common.ExceptionLogRepository;
import kz.btsd.edmarket.user.model.EmailVerification;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.EmailVerificationRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static kz.btsd.edmarket.user.listener.UserEmailListener.REPLACE_SITE_URL;

@Service
public class UserEmailSender {
    @Value("classpath:email/confirm.html")
    private Resource confirmEmailContent;
    @Autowired
    private JavaMailSenderImpl sender;
    @Value("classpath:email/send-code.html")
    private Resource sendCodeEmailContent;
    @Value("${jumysbar.email.enabled}")
    private boolean emailEnabled;
    @Value("${spring.profiles}")
    private String profiles;
    @Value("${jumysbar.sms.enabled}")
    boolean smsEnabled;
    @Autowired
    private ExceptionLogRepository exceptionLogRepository;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    public static final String REPLACE_CODE = "&replace-code&";


    public String frontEndUrl(Platform platform) {
        if (profiles.equals("development")) {
            return "https://dev.jumysbar.kz";
        }
        if (profiles.equals("production")) {
            switch (platform) {
                case ERG:
                    return "https://ergdu.jumysbar.kz";
                case BTSD:
                    return "https://btsd.jumysbar.kz";
                case DEMO:
                    return "https://demo.jumysbar.kz";
                case ECOMMERCE:
                    return "https://ecommerce.jumysbar.kz";
                case CMTIS:
                    return "https://e.cmtis.kz";
                default:
                    return "https://aitu.jumysbar.kz";

            }
        }
        return "https://aitu.jumysbar.kz";
    }


    private String getConfirmHtml() {
        String html = "";
        try {
            html = IOUtils.toString(confirmEmailContent.getInputStream(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    public void sendConfirmEmail(User user, String uuid) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Подтверждение почты для Jumysbar.kz");
            String html = getConfirmHtml();
            html = html.replace(REPLACE_SITE_URL, frontEndUrl(user.getPlatform()) + "/confirmation-email?userId=" + user.getId() + "&uuid=" + uuid);
            helper.setText(html, true);
            sender.send(message);
        } catch (Exception e) {
            exceptionLogRepository.save(new ExceptionLog(e.getMessage(), ExceptionUtils.getStackTrace(e)));
            e.printStackTrace();
        }
    }

    public void processSendConfirmEmail(User user) {
        if (emailEnabled) {
            String uuid = UUID.randomUUID().toString();
            EmailVerification emailVerification = new EmailVerification(user.getId(), uuid);
            emailVerificationRepository.save(emailVerification);
            sendConfirmEmail(user, uuid);
        }
    }

    private String getHtml() {
        String html = "";
        try {
            html = IOUtils.toString(sendCodeEmailContent.getInputStream(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    public void sendEmail(String email, Long code) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Jumysbar.kz проверочный код");
            String html = getHtml();
            html = html.replace(REPLACE_CODE, code.toString());
            helper.setText(html, true);
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
