package kz.btsd.edmarket.event.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EventStatusRequest {
    @NotNull
    private EventStatus status;
    @NotNull
    private Long eventId;
    //todo причина блокировки- пока обязательна
    private String reason;
}
