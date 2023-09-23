package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.event.model.EventResponse;
import lombok.Data;

import java.util.List;

/**
 * Таблица успеваемости
 */
@Data
public class EventProgressUsersDto {
    private EventResponse eventResponse;
    //@JsonIgnore
    // private List<EventProgressDto> eventProgresses;
    private List<EventProgressFullStatsDto> eventProgresses;

    public EventProgressUsersDto() {
    }
}
