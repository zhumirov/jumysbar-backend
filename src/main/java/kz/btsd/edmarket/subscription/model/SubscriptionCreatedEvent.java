package kz.btsd.edmarket.subscription.model;

import org.springframework.context.ApplicationEvent;

public class SubscriptionCreatedEvent extends ApplicationEvent {
    private final Subscription subscription;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SubscriptionCreatedEvent(Object source, Subscription subscription) {
        super(source);
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }
}
