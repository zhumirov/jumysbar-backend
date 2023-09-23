package kz.btsd.edmarket.mentor.service;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.mentor.model.Mentor;
import kz.btsd.edmarket.mentor.repository.MentorRepository;
import kz.btsd.edmarket.notification.service.MentorCreatedEvent;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.subscription.service.SubscriptionService;
import kz.btsd.edmarket.user.listener.UserCreatedEvent;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MentorService {
    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ApplicationEventPublisher publisher;

    private void save(Mentor mentor) {
        if (!mentorRepository.existsByPhoneAndEventId(mentor.getPhone(), mentor.getEventId())) {
            Optional<User> optionalUser = userRepository.findByPhoneAndDeletedFalse(mentor.getPhone());
            if (optionalUser.isPresent()) {
                mentor.setUserId(optionalUser.get().getId());
                if (!subscriptionRepository.existsByEventIdAndUserId(mentor.getEventId(), mentor.getUserId())) {
                    subscriptionService.createAndSaveSubscription(mentor.getEventId(), optionalUser.get().getId(), 0L, null);
                }
            }
            mentorRepository.save(mentor);
        }
    }

    public void saveWithParent(Mentor mentor) {
        save(mentor);
        Optional<Event> parentEvent = eventRepository.findByParentId(mentor.getEventId());
        if (parentEvent.isPresent()) {
            save(new Mentor(mentor.getPhone(), parentEvent.get().getId()));
        }
        publisher.publishEvent(new MentorCreatedEvent(this, parentEvent.get().getUserId(), mentor));
    }

    @Async
    @EventListener
    public void processUserCreatedEvent(UserCreatedEvent event) {
        mentorRepository.updateMentorUserId(event.getUser().getId(), event.getUser().getPhone());
    }
}
