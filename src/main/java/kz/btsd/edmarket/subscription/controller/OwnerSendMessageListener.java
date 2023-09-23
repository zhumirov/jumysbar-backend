package kz.btsd.edmarket.subscription.controller;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.subscription.model.EmailMessage;
import kz.btsd.edmarket.subscription.model.SendMessageToSubscribersEvent;
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
import java.util.LinkedList;
import java.util.List;

@Service
public class OwnerSendMessageListener {
    @Autowired
    private JavaMailSenderImpl sender;
    @Value("classpath:email/event-message.html")
    private Resource emailContent;
    @Value("${jumysbar.email.enabled}")
    private boolean emailEnabled;
    @Value("${jumysbar.backend.url}")
    private String backendUrl;
    @Value("${jumysbar.site.url}")
    private String siteUrl;
    @Value("${jumysbar.email.photo.default}")
    private String defaultPhoto;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    public static final String REPLACE_MESSAGE = "&replace-message&";
    public static final String REPLACE_EVENT_LINK = "&replace-event-link&";
    public static final String REPLACE_OWNER_PHOTO = "&replace-owner-photo&";
    public static final String REPLACE_OWNER_NAME = "&replace-owner-name&";

    @Async
    @EventListener
    public void processTransferMoneyCreatedEvent(SendMessageToSubscribersEvent event) {
        if (emailEnabled) {
            List<User> users = userRepository.findAllSubscriptionUser(event.getEmailMessage().getEventId());
            List<String> operatorsEmail = new LinkedList<>();
            for (User operator : users) {
                if (operator.getEmail() != null) { //todo надо ли, посмотреть обязателен ли он
                    operatorsEmail.add(operator.getEmail());
                }
            }
            String[] emails = new String[users.size()];
            emails = operatorsEmail.toArray(emails);
            sendEmail(event.getEmailMessage(), emails);
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

    private String getAndReplaceHtml(EmailMessage emailMessage) {
        User owner = userRepository.findById(emailMessage.getUserId()).get();
        String html = getHtml();
        html = html.replace(REPLACE_MESSAGE, emailMessage.getMessage());
        html = html.replace(REPLACE_EVENT_LINK, siteUrl + "/course/" + emailMessage.getEventId());
        if (owner.getName() != null) {
            html = html.replace(REPLACE_OWNER_NAME, owner.getName());
        }
        {
            html = html.replace(REPLACE_OWNER_NAME, "");
        }
        if (owner.getFileId() != null) {
            html = html.replace(REPLACE_OWNER_PHOTO, backendUrl + "/files/" + owner.getFileId());
        } else {
            html = html.replace(REPLACE_OWNER_PHOTO, defaultPhoto);
        }
        return html;
    }

    //todo обработка ошибок
    public void sendEmail(EmailMessage emailMessage, String[] sendToUserEmail) {
        Event event = eventRepository.findById(emailMessage.getEventId()).get();

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject("Вам сообщение по курсу " + event.getTitle());
            helper.setText(getAndReplaceHtml(emailMessage), true);
            helper.setTo(sendToUserEmail);
//            todo заменить или удалить
//            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
