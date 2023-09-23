package kz.btsd.edmarket.invitation.service;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DeleteSubscriptionListener {
    public static final String REPLACE_USERNAME = "&replace-username&";
    public static final String REPLACE_EVENT_TITLE = "&replace-event-title&";

    @Value("classpath:email/delete-invite.html")
    private Resource emailContent;

    @Autowired
    private JavaMailSenderImpl sender;

    @Async
    @EventListener
    public void processInvitation(DeleteSuscriptionEvent deleteSuscriptionEvent) {
        DeleteSubscription deleteSubscription = deleteSuscriptionEvent.getDeleteSubscription();
        if (deleteSubscription.getEmail() != null) {
            sendEmail(deleteSubscription.getEventTitle(), deleteSubscription.getUserName(), deleteSubscription.getEmail());
        }
    }

    public void sendEmail(String title, String name, String email) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject("Убран доступ на курс");
            helper.setText(getAndReplaceHtml(title, name), true);
            helper.setTo(email);
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getHtml() {
        String html = "";
        try {
            html = IOUtils.toString(emailContent.getInputStream(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    private String getAndReplaceHtml(String title, String username) {
        String html = getHtml();
        html = html.replace(REPLACE_USERNAME, username);
        html = html.replace(REPLACE_EVENT_TITLE, title);
        return html;
    }
}
