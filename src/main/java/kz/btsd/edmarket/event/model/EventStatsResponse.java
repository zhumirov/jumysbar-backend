package kz.btsd.edmarket.event.model;

import lombok.Data;

import java.util.Map;

@Data
public class EventStatsResponse {
    private Long maxPrice;
    private Long minPrice;
}
