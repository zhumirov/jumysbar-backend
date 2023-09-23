package kz.btsd.edmarket.event.moderation;

import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.user.model.UserShortDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ModerationRow {
    private EventModeration eventModeration;
    private UserShortDto user;
    private EventTitleDto event;

    public ModerationRow(EventModeration eventModeration, UserShortDto user, EventTitleDto event) {
        this.eventModeration = eventModeration;
        this.user = user;
        this.event = event;
    }
}
