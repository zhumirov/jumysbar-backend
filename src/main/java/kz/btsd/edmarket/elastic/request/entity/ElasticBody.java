package kz.btsd.edmarket.elastic.request.entity;

import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.user.model.Platform;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Arrays;
import java.util.List;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html
 */
@Data
public class ElasticBody {
    private String query;
    //преподаватель
    private List<Long> userIds;
    private Integer from;
    private Integer size;
    private Long minPrice;
    private Long maxPrice;
    private List<Platform> platforms = Arrays.asList(Platform.JUMYSBAR);
    //Статус
    private List<EventStatus> statuses = Arrays.asList(EventStatus.APPROVED);
    //Категории
    private List<Long> categories;
    //Для кого
    private List<Long> levels;
    //сушествует сертификат
    private Boolean cert;
    //бесплатный курс
    private Boolean free;
    private String sort;
}
