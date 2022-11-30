package top.b0x0.demo.es.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import top.b0x0.demo.es.SpringBootElasticsearchDemoApplicationTests;

import java.io.IOException;

public class RestHighLevClientForDocumentSearch2Tests extends SpringBootElasticsearchDemoApplicationTests {

    @Qualifier("elasticsearchClient")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testSearch() throws IOException {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(0) //起始位置 start = (page-1)*size
                .size(10);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("business", "Apple"));
        boolQueryBuilder.must(QueryBuilders.termQuery("title", "iphone13"));

        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        boolQueryBuilder2.must(QueryBuilders.termQuery("business", "Xiaomi"));
        boolQueryBuilder2.must(QueryBuilders.termQuery("title", "Xiaomi 12"));

        BoolQueryBuilder boolQueryBuilder3 = QueryBuilders.boolQuery();
        boolQueryBuilder3.must(QueryBuilders.termQuery("business", "Huawei"));

        BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
        shouldQuery
                .should(boolQueryBuilder)
                .should(boolQueryBuilder2)
                .should(boolQueryBuilder3)
                .minimumShouldMatch(1);

        BoolQueryBuilder filter = QueryBuilders.boolQuery().filter(shouldQuery);

        sourceBuilder.query(filter);

        System.out.println("sourceBuilder = " + sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] hits = searchResponse.getHits().getHits();
        int length = hits.length;
        System.out.println("length = " + length);
        for (SearchHit hit : hits) {
            System.out.println("id: " + hit.getId() + " source: " + hit.getSourceAsString());
        }
    }

    @Test
    public void testCount() throws IOException {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("business", "Apple"));
        boolQueryBuilder.must(QueryBuilders.termQuery("title", "iphone13"));

        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        boolQueryBuilder2.must(QueryBuilders.termQuery("business", "Xiaomi"));
        boolQueryBuilder2.must(QueryBuilders.termQuery("title", "Xiaomi 12"));

        BoolQueryBuilder boolQueryBuilder3 = QueryBuilders.boolQuery();
        boolQueryBuilder3.must(QueryBuilders.termQuery("business", "Huawei"));

        BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
        shouldQuery
                .should(boolQueryBuilder)
                .should(boolQueryBuilder2)
                .should(boolQueryBuilder3)
                .minimumShouldMatch(1);


        System.out.println("shouldQuery.toString() = " + shouldQuery.toString());

        CountRequest searchRequest = new CountRequest("products");
        searchRequest.query(shouldQuery);

        CountResponse countResponse = restHighLevelClient.count(searchRequest, RequestOptions.DEFAULT);
        long count = countResponse.getCount();
        System.out.println("count = " + count);
    }


}
