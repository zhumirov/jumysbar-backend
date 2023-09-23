package kz.btsd.edmarket.elastic;

import elastic.EventDto;
import elastic.EventElasticsearchRepository;
import kz.btsd.edmarket.certificate.repository.CertificateSettingsRepository;
import kz.btsd.edmarket.certificate.service.CertificateSettingsService;
import kz.btsd.edmarket.elastic.request.entity.ElasticBody;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventConverter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class EventElasticService {
    @Value("${spring.elasticsearch.rest.uris}")
    private String elasticURL;
    @Value("${spring.elasticsearch.rest.username}")
    private String username;
    @Value("${spring.elasticsearch.rest.password}")
    private String password;
    @Autowired
    private Environment env;
    @Autowired
    private EventConverter eventConverter;
    @Autowired
    private EventElasticsearchRepository eventElasticsearchRepository;
    @Autowired
    private CertificateSettingsService certificateSettingsService;

    private RestHighLevelClient client;

    @PostConstruct
    public void init() {
        // basic auth, preemptive authentication
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        final RestClientBuilder builder = RestClient.builder(HttpHost.create(elasticURL));
        //todo переработать для production, когда будет логин и пароль
        if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("development")) { //todo проработать и для дева после перезда на новый хост
            builder.setHttpClientConfigCallback(b -> b.setDefaultCredentialsProvider(credentialsProvider));
        }
        client = new RestHighLevelClient(builder);
    }

   // @Async
    public void publish(Event event) {
        EventDto eventDto = eventConverter.convertToDto(event);
        eventDto.setFree(!isNotEmpty(event.getPlans()));//бесплатный курс
        eventDto.setCert(certificateSettingsService.findByEvenId(event.getId()).isPresent());//существует сертификат
        eventElasticsearchRepository.save(eventDto);
    }

    public void publishAll(List<Event> events) {
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event : events) {
            eventDtos.add(eventConverter.convertToDto(event));
        }
        eventElasticsearchRepository.saveAll(eventDtos);
    }

    public void deleteAll() {
        eventElasticsearchRepository.deleteAll();
    }

    public void reloadAll() {
        eventElasticsearchRepository.deleteAll();
    }

    @Async
    public void publishDelete(Long id) {
        eventElasticsearchRepository.deleteById(id);
    }

    public SearchHits search(ElasticBody elasticBody) throws IOException {
        SearchRequest searchRequest = createSearchRequest(elasticBody);
        final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits();
    }

    public SearchHits searcAsYour(String query) throws IOException {
        final MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery(query, "title",
                "title._2gram",
                "title._3gram").type("bool_prefix");
        SearchRequest searchRequest = new SearchRequest(EventDto.INDEX_NAME);
        searchRequest.source().query(qb);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits();
    }

    public SearchRequest createSearchRequest(ElasticBody elasticBody) {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (elasticBody.getQuery() != null && elasticBody.getQuery().length() > 0) { //todo переделать на isBlank, когда включу 11 java
            final QueryBuilder multiQuery = QueryBuilders
                    .multiMatchQuery(elasticBody.getQuery(), "title",
                            "shortDescription", "description",
                            "courseOutline", "acquiredSkills", "certification",
                            "user.name")
                    .fuzziness(Fuzziness.TWO)
                    .slop(2);
            boolQueryBuilder = boolQueryBuilder
                    .must(multiQuery);
        }
        if (isNotEmpty(elasticBody.getUserIds())) {
            boolQueryBuilder = boolQueryBuilder
                    .must(termsQuery("userId", elasticBody.getUserIds()));
        }
        if (elasticBody.getCert() != null) {
            boolQueryBuilder = boolQueryBuilder
                    .must(matchQuery("cert", elasticBody.getCert()));
        }
        if (elasticBody.getFree() != null) {
            boolQueryBuilder = boolQueryBuilder
                    .must(matchQuery("free", elasticBody.getFree()));
        }
        if (elasticBody.getMinPrice() != null) {
            boolQueryBuilder = boolQueryBuilder
                    .must(rangeQuery("price").gte(elasticBody.getMinPrice()));
        }
        if (elasticBody.getMaxPrice() != null) {
            boolQueryBuilder = boolQueryBuilder
                    .must(rangeQuery("price").lte(elasticBody.getMaxPrice()));
        }
        if (isNotEmpty(elasticBody.getPlatforms())) {
            boolQueryBuilder = boolQueryBuilder
                    .must(termsQuery("platform.keyword", elasticBody.getPlatforms()));
        }
        if (isNotEmpty(elasticBody.getStatuses())) {
            boolQueryBuilder = boolQueryBuilder
                    .must(termsQuery("status.keyword", elasticBody.getStatuses()));
        }
        if (isNotEmpty(elasticBody.getCategories())) {
            boolQueryBuilder = boolQueryBuilder
                    .must(termsQuery("categoryId", elasticBody.getCategories()));
        }
        if (isNotEmpty(elasticBody.getLevels())) {
            boolQueryBuilder = boolQueryBuilder
                    .must(termsQuery("levelId", elasticBody.getLevels()));
        }
        SortBuilder sortBuilder;
        if (elasticBody.getSort() != null) {
            sortBuilder = SortBuilders.fieldSort("startDateTime").order(SortOrder.fromString(elasticBody.getSort()));
        } else {
            sortBuilder = SortBuilders.fieldSort("startDateTime").order(SortOrder.ASC);
        }
        QueryBuilder combinedBoolQuery = boolQueryBuilder;
        int size = 100;
        if (elasticBody.getSize() != null) {
            size = elasticBody.getSize();
        }
        int from = 0;
        if (elasticBody.getFrom() != null) {
            from = elasticBody.getFrom();
        }
        searchSourceBuilder
                .query(combinedBoolQuery)
                .from(from)
                .size(size);
              //  .sort(sortBuilder);
        return new SearchRequest(EventDto.INDEX_NAME).source(searchSourceBuilder);
    }
}
