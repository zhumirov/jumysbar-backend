package kz.btsd.edmarket.subscription.controller;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.subscription.model.EmailMessage;
import kz.btsd.edmarket.subscription.model.EmailMessageResponse;
import kz.btsd.edmarket.subscription.model.SendMessageToSubscribersEvent;
import kz.btsd.edmarket.subscription.repository.EmailMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class SendMessageToSubscribersController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private EmailMessageRepository emailRepository;

    public boolean checkDate(Date date) {
        LocalDate lastDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        if (lastDate.plusDays(1).compareTo(LocalDate.now()) <= 0) {
            return true;
        } else {
            return false;
        }
    }

    @PostMapping("/subscriptions/email")
    public EmailMessageResponse allByEventId(@RequestBody EmailMessage emailMessage) {
        Optional<EmailMessage> lastEmailMessage = emailRepository.findFirstByEventIdOrderByCreatedDateDesc(emailMessage.getEventId());
        if (lastEmailMessage.isPresent() && !checkDate(lastEmailMessage.get().getCreatedDate())) {
            return new EmailMessageResponse(false, "можно отправлять не чаще раз в день");

        } else {
            Event event = eventRepository.findById(emailMessage.getEventId()).get();
            emailMessage.setUserId(event.getUserId());
            emailMessage = emailRepository.save(emailMessage);
            publisher.publishEvent(new SendMessageToSubscribersEvent(this, emailMessage));
            return new EmailMessageResponse(true);
        }
    }
}
