package elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EventElasticsearchRepository extends ElasticsearchRepository<EventDto, Long> {
}
