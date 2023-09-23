package kz.btsd.edmarket.subscription.model;

import org.springframework.context.ApplicationEvent;

public class SendMessageToSubscribersEvent extends ApplicationEvent {
    private final EmailMessage emailMessage;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SendMessageToSubscribersEvent(Object source, EmailMessage emailMessage) {
        super(source);
        this.emailMessage = emailMessage;
    }

    public EmailMessage getEmailMessage() {
        return emailMessage;
    }
}
