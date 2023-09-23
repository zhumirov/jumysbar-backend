package kz.btsd.edmarket.notification.service;

import kz.btsd.edmarket.mentor.model.Mentor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MentorCreatedEvent extends ApplicationEvent {
    private final Mentor mentor;
    private final Long senderId;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MentorCreatedEvent(Object source, Long senderId, Mentor mentor) {
        super(source);
        this.mentor = mentor;
        this.senderId = senderId;
    }
}
