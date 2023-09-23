package kz.btsd.edmarket.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.btsd.edmarket.event.model.Event;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.rest.RestStatus.CREATED;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-create-index.html
 */
public class SearchAsYouYypeServiceTest {
    private static final String INDEX = "test_events";
    private static RestHighLevelClient client;
    private EventElasticService eventElasticService = new EventElasticService();
    private static final ElasticsearchContainer container =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.6.2").withExposedPorts(9200);

    @BeforeAll
    public static void startElasticsearchCreateCloudClient() throws IOException {
        container.start();
        HttpHost host = new HttpHost("localhost", container.getMappedPort(9200));
        // HttpHost host = new HttpHost("localhost", 9200);

        final RestClientBuilder builder = RestClient.builder(host);
        client = new RestHighLevelClient(builder);
        createIndex();
    }

    @AfterAll
    public static void closeResources() throws Exception {
        client.close();
    }

    private static void createIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(INDEX);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (exists) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX);
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        }
        CreateIndexRequest request = new CreateIndexRequest(INDEX);
        request.mapping(mapping());
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
    }

    private static XContentBuilder mapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("title");
                {
                    builder.field("type", "search_as_you_type");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Test
    void createIndexSearchAsYouType() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(INDEX);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (exists) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX);
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            assertThat(deleteIndexResponse.isAcknowledged());
        }
        CreateIndexRequest request = new CreateIndexRequest(INDEX);
        request.mapping(mapping());
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        assertThat(acknowledged);
    }

    private IndexRequest indexRequest(Event event) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final byte[] bytes = mapper.writeValueAsBytes(event);
        final IndexRequest request = new IndexRequest(INDEX);
        if (event.getId() != null) {
            request.id(String.valueOf(event.getId()));
        }
        request.source(bytes, XContentType.JSON);
        return request;
    }

    @Test
    public void testQueryBuilders() throws Exception {
        List<Event> list = new LinkedList<>();
        Event event1 = new Event();
        event1.setTitle("Java ee course");
        event1.setId(1l);
        list.add(event1);
        Event event2 = new Event();
        event2.setTitle("Spring boot course");
        event2.setId(2l);
        list.add(event2);
        Event event3 = new Event();
        event3.setTitle("English online course");
        event3.setId(3l);
        list.add(event3);
        Event event4 = new Event();
        event4.setTitle("English skyeng");
        event4.setId(4l);
        list.add(event4);

        for (Event event : list) {
            IndexResponse indexResponse = client.index(indexRequest(event), RequestOptions.DEFAULT);
            assertThat(indexResponse.status().equals(CREATED));
        }
        //client.indices().refresh(new RefreshRequest(), RequestOptions.DEFAULT);
        GetRequest getRequest = new GetRequest(INDEX, "3");
        client.get(getRequest, RequestOptions.DEFAULT);
        String query = "English";
        for (int i = 2; i < query.length(); i++) {
            final MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery(query.substring(0, i), "title",
                    "title._2gram",
                    "title._3gram", "title._index_prefix").type("bool_prefix");
            SearchRequest searchRequest = new SearchRequest(INDEX);
            searchRequest.source().query(qb);
            final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("size i=" + i + " " + response.getHits().getHits().length);
            assertThat(response.getHits().getTotalHits().value).isGreaterThanOrEqualTo(1);
        }

        for (int i = 2; i < query.length(); i++) {
            MatchPhrasePrefixQueryBuilder qb = QueryBuilders.matchPhrasePrefixQuery("title", query.substring(0, i));
            SearchRequest searchRequest = new SearchRequest(INDEX);
            searchRequest.source().query(qb);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("sizePhrase i=" + i + " " + response.getHits().getHits().length);
            assertThat(response.getHits().getTotalHits().value).isGreaterThanOrEqualTo(1);
        }

        final BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.multiMatchQuery("Spring", "title",
                        "title._2gram",
                        "title._3gram").type("bool_prefix"));

        SearchRequest searchRequest = new SearchRequest(INDEX);
        searchRequest.source().query(qb);
        final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        // exact hit count
        assertThat(response.getHits().getTotalHits().value).isEqualTo(1);
        // assertThat(response.getHits().getTotalHits().relation).isEqualTo(TotalHits.Relation.EQUAL_TO);

        // first hit should be 2010 edition due to its price and the above should clause
        final SearchHit[] hits = response.getHits().getHits();
    }
}
