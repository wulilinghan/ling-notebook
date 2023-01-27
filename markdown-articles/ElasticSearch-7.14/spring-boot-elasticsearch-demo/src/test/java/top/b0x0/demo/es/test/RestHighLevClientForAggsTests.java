package top.b0x0.demo.es.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.b0x0.demo.es.SpringBootElasticsearchDemoApplicationTests;

import java.io.IOException;
import java.util.List;

public class RestHighLevClientForAggsTests extends SpringBootElasticsearchDemoApplicationTests {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public RestHighLevClientForAggsTests(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * max(ParsedMax) min(ParsedMin) sum(ParsedSum) avg(ParsedAvg) 聚合函数  桶中只有一个返回值
     */
    @Test
    public void testAggsFunction() throws IOException {
        SearchRequest searchRequest = new SearchRequest("fruit");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
                .query(QueryBuilders.matchAllQuery()) //查询条件
                //.aggregation(AggregationBuilders.sum("sum_price").field("price"))//用来设置聚合处理 sum
                //.aggregation(AggregationBuilders.avg("avg_price").field("price")) //用来设置聚合处理 avg
                .aggregation(AggregationBuilders.max("max_price").field("price")) //max
                .size(0);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations = searchResponse.getAggregations();
        ParsedMax parsedMax = aggregations.get("max_price");
        System.out.println(parsedMax.getValue());


    }


    /**
     * 基于 terms 类型进行聚合 基于字段进行分组聚合
     */
    @Test
    void testAggs() throws IOException {
        SearchRequest searchRequest = new SearchRequest("fruit");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
                .query(QueryBuilders.matchAllQuery()) //查询条件
                .aggregation(AggregationBuilders.terms("title_group").field("title"))//用来设置聚合处理
                .size(0);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //处理聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedStringTerms parsedStringTerms = aggregations.get("title_group");

        List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKey() + "   " + bucket.getDocCount());
        }


    }

    @Test
    void test_Aggs2() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gt("4000");

        sourceBuilder
                .query(rangeQueryBuilder) //查询条件
                .aggregation(AggregationBuilders.terms("business_group").field("business"))//用来设置聚合处理
                .size(0);
        searchRequest.source(sourceBuilder);

        System.out.println("sourceBuilder DSL  = " + sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //处理聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedStringTerms parsedStringTerms = aggregations.get("business_group");

        List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKey() + "   " + bucket.getDocCount());
        }


    }

}
