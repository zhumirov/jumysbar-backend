package kz.btsd.edmarket.invitation.service;

import org.springframework.context.ApplicationEvent;

public class DeleteSuscriptionEvent extends ApplicationEvent {

    private final DeleteSubscription deleteSubscription;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DeleteSuscriptionEvent(Object source, DeleteSubscription deleteSubscription) {
        super(source);
        this.deleteSubscription = deleteSubscription;
    }

    public DeleteSubscription getDeleteSubscription() {
        return deleteSubscription;
    }
}
