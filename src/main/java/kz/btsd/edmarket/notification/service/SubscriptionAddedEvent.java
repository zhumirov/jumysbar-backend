package kz.btsd.edmarket.notification.service;

import kz.btsd.edmarket.subscription.model.Subscription;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SubscriptionAddedEvent extends ApplicationEvent {
    private final Subscription subscription;
    private final Long senderId;


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SubscriptionAddedEvent(Object source, Long senderId, Subscription subscription) {
        super(source);
        this.subscription = subscription;
        this.senderId = senderId;
    }
}
