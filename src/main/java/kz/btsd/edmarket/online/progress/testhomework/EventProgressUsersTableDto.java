package kz.btsd.edmarket.online.progress.testhomework;

import kz.btsd.edmarket.event.model.EventResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Таблица успеваемости
 */
@Data
public class EventProgressUsersTableDto {
    private EventResponse eventResponse;
    private List<EventProgressUserRow> eventProgresses;

    private long totalHits;

    public EventProgressUsersTableDto() {
    }
}
