package kz.btsd.edmarket.invitation.service;

import kz.btsd.edmarket.invitation.model.InvitationEntity;
import org.springframework.context.ApplicationEvent;

public class InvitationEvent extends ApplicationEvent {

    private final InvitationEntity invitationEntity;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public InvitationEvent(Object source, InvitationEntity invitationEntity) {
        super(source);
        this.invitationEntity = invitationEntity;
    }

    public InvitationEntity getInvitationEntity() {
        return invitationEntity;
    }
}
