package kz.btsd.edmarket.elastic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import elastic.EventDto;
import kz.btsd.edmarket.elastic.json.EventSearchResponse;
import kz.btsd.edmarket.elastic.request.entity.ElasticBody;
import kz.btsd.edmarket.elastic.request.entity.ElasticSuggestRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class ElasticSearchController {
    @Autowired
    private EventElasticService eventElasticService;

    @RequestMapping(value = "/search/events/autocomplete", method = {RequestMethod.GET, RequestMethod.POST})
    public EventSearchResponse suggestSearch(@RequestBody ElasticSuggestRequest request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
        SearchHits searchHits = eventElasticService.searcAsYour(request.getQuery());
        List<EventDto> list = new LinkedList<>();
        for (SearchHit searchHit : searchHits) {
            EventDto eventDto = objectMapper.readValue(searchHit.getSourceAsString(), EventDto.class);
            list.add(eventDto);
        }
        return new EventSearchResponse(searchHits.getTotalHits().value, list);
    }

    @RequestMapping(value = "/search/events", method = {RequestMethod.GET, RequestMethod.POST})
    public EventSearchResponse search(@RequestBody ElasticBody elasticBody) throws IOException {
        elasticBody.getPlatforms().remove(null); //todo удаление null, ошибки в логах
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
        SearchHits searchHits = eventElasticService.search(elasticBody);
        List<EventDto> list = new LinkedList<>();
        for (SearchHit searchHit : searchHits) {
            EventDto eventDto = objectMapper.readValue(searchHit.getSourceAsString(), EventDto.class);
            list.add(eventDto);
        }
        return new EventSearchResponse(searchHits.getTotalHits().value, list);
    }
}
