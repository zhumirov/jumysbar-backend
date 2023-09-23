package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.subscription.model.Subscription;
import kz.btsd.edmarket.subscription.model.SubscriptionCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ProgressSubscriptionListener {
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private EventRepository eventRepository;

    @Async
    @EventListener
    public void processSubscriptionCreatedEvent(SubscriptionCreatedEvent subscriptionCreatedEvent) {
        Subscription subscription = subscriptionCreatedEvent.getSubscription();
        Optional<EventProgress> optional = eventProgressRepository.findByEventIdAndUserId(subscription.getEventId(), subscription.getUserId());
        if (!optional.isPresent()) {
            EventProgress eventProgress = new EventProgress();
            eventProgress.setUserId(subscription.getUserId());
            eventProgress.setEventId(subscription.getEventId());
            eventProgress.setStartDate(LocalDateTime.now());
            eventProgressRepository.save(eventProgress);
        }
    }
}
