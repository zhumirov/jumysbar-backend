package kz.btsd.edmarket.event.model;

import elastic.EventDto;
import kz.btsd.edmarket.event.moderation.EventModeration;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import lombok.Data;

import java.util.List;

@Data
public class EventResponse {
    private EventDto event;
    private CommentRating commentRating;
    private List<ModuleDto> modules;
    private EventModeration moderation;
    private boolean signed = false;
    private Long lastStepId;
    private EventStatus childStatus;
}
