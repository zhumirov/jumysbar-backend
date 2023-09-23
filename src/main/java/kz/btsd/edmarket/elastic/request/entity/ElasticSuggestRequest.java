package kz.btsd.edmarket.elastic.request.entity;

import lombok.Data;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html
 */
@Data
public class ElasticSuggestRequest {
    private String query;
}
