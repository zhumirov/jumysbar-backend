package kz.btsd.edmarket.notification.service;

import kz.btsd.edmarket.comment.model.Comment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentCreatedEvent extends ApplicationEvent {
    private final Comment comment;
    private final Long senderId;


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public CommentCreatedEvent(Object source, Long senderId, Comment comment) {
        super(source);
        this.comment = comment;
        this.senderId = senderId;
    }
}
