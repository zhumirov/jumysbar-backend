package kz.btsd.edmarket.payment.check;

import kz.btsd.edmarket.payment.check.model.KassaCheck;
import kz.btsd.edmarket.subscription.model.Subscription;
import kz.btsd.edmarket.subscription.model.SubscriptionCreatedKassaEvent;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
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
public class KassaListener {
    public static final String REPLACE_EVENT_CHECK_LINK = "&replace-event-check&";
    @Value("${jumysbar.site.url}")
    private String siteUrl;
    @Value("${jumysbar.onlinekassa.enabled}")
    private boolean kassaEnabled;
    @Value("classpath:email/event-invite-check.html")
    private Resource emailContent;
    @Autowired
    private JavaMailSenderImpl sender;
    @Autowired
    private OnlineKassaService onlineKassaService;
    @Autowired
    private UserRepository userRepository;

    @Async
    @EventListener
    public void processInvitation(SubscriptionCreatedKassaEvent subscriptionCreatedKassaEvent) {
        if (kassaEnabled) {
            Subscription subscription = subscriptionCreatedKassaEvent.getSubscription();
            KassaCheck kassaCheck = onlineKassaService.kkmsSales(subscription);
            User user = userRepository.findById(subscription.getUserId()).get();
            sendEmail(user.getEmail(), kassaCheck);
        }
    }

    public void sendEmail(String email, KassaCheck kassaCheck) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject("Jumysbar.kz Кассовый чек");
            helper.setText(getAndReplaceHtml(kassaCheck), true);
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

    private String getAndReplaceHtml(KassaCheck kassaCheck) {
        String html = getHtml();
        html = html.replace(REPLACE_EVENT_CHECK_LINK, kassaCheck.getReceipt());
        return html;
    }
}
