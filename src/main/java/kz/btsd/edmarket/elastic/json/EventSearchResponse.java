package kz.btsd.edmarket.elastic.json;

import elastic.EventDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class EventSearchResponse {
    private long totalHits;
    private List<EventDto> list;
}
