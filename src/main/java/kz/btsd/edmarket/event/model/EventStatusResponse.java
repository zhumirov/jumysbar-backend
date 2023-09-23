package kz.btsd.edmarket.event.model;

import kz.btsd.edmarket.event.moderation.EventModeration;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EventStatusResponse {
    private boolean changed = false;
    private Long publishedId;
    private EventModeration moderation;

    public EventStatusResponse(boolean changed) {
        this.changed = changed;
    }
    public EventStatusResponse(boolean changed, Long publishedId) {
        this.changed = changed;
        this.publishedId = publishedId;
    }
}
