package kz.btsd.edmarket.notification.service;

import kz.btsd.edmarket.event.model.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LessonAddedEvent extends ApplicationEvent {
    private final Event event;
    private final Long senderId;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public LessonAddedEvent(Object source, Long senderId, Event event) {
        super(source);
        this.event = event;
        this.senderId = senderId;
    }
}
