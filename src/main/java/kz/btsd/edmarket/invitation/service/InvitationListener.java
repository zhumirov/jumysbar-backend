package kz.btsd.edmarket.invitation.service;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.invitation.model.InvitationEntity;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.UserEmailSender;
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
import java.util.Optional;

@Service
public class InvitationListener {

    public static final String COURSE_URI = "/online-course/";
    public static final String REPLACE_EVENT_LINK = "&replace-event-link&";
    public static final String REPLACE_USERNAME = "&replace-username&";
    public static final String REPLACE_EVENT_TITLE = "&replace-event-title&";
    public static final String REPLACE_EVENT_OWNERNAME = "&replace-event-ownername&";

    @Value("classpath:email/event-invite.html")
    private Resource emailContent;

    @Autowired
    private JavaMailSenderImpl sender;
    //todo вынести отдельный сервис для всех рассылок
    @Autowired
    private UserEmailSender userEmailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    @Async
    @EventListener
    public void processInvitation(InvitationEvent invitationEvent) {
        InvitationEntity invitationEntity = invitationEvent.getInvitationEntity();
        if (invitationEntity.getEmail() != null) {
            sendEmail(invitationEntity.getEmail(), invitationEntity.getEventId());

        }
    }

    public void sendEmail(String email, Long eventId) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject("Приглашение на курс");
            helper.setText(getAndReplaceHtml(email, eventId), true);
            helper.setTo(email);
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEventLink(Long eventId, Platform platform) {
        return userEmailSender.frontEndUrl(platform) + COURSE_URI + eventId;
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

    private String getAndReplaceHtml(String email, Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        User owner = userRepository.findById(event.getUserId()).get();
        String username=email;
        Optional<User> optional = userRepository.findByEmailAndDeletedFalse(email);
        if (optional.isPresent()) {
            username = optional.get().getName();
        }
        String html = getHtml();
        html = html.replace(REPLACE_EVENT_LINK, getEventLink(eventId, event.getPlatform()));
        html = html.replace(REPLACE_USERNAME, username);
        html = html.replace(REPLACE_EVENT_TITLE, event.getTitle());
        html = html.replace(REPLACE_EVENT_OWNERNAME, owner.getName());
        return html;
    }
}
