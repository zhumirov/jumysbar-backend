package kz.btsd.edmarket.elastic;

import kz.btsd.edmarket.elastic.request.entity.ElasticBody;
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
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class EventElasticServiceTest {
    private static final String INDEX = "events2";
    private static RestHighLevelClient client;
    private EventElasticService eventElasticService = new EventElasticService();

    @BeforeAll
    public static void startElasticsearchCreateCloudClient() {
        String uris = "https://search-edmarket-dev-adzytrhvlzt7k2wukfntoryyta.eu-central-1.es.amazonaws.com";
        String user = "elastic";
        String password = "";

        // basic auth, preemptive authentication
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));

        final RestClientBuilder builder = RestClient.builder(HttpHost.create(uris));
        // builder.setHttpClientConfigCallback(b -> b.setDefaultCredentialsProvider(credentialsProvider));

        client = new RestHighLevelClient(builder);
    }

    @Test
    void createSearchRequest() throws IOException {
        ElasticBody elasticBody = new ElasticBody();
        //elasticBody.setQuery("java");
        elasticBody.setFrom(0);
        elasticBody.setSize(10);
        //   elasticBody.setType(MASTER_CLASS);
        //elasticBody.setPrice(new Range("3000", "9000"));
        SearchRequest searchRequest = eventElasticService.createSearchRequest(elasticBody);
        final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        final List<String> ids = Arrays.stream(response.getHits().getHits()).map(SearchHit::getId).collect(Collectors.toList());
        for (String str : ids) {
            System.out.println(str);
        }
        assertThat(ids.size()).isNotNull();
        assertThat(ids).hasSize(2);
    }
}
