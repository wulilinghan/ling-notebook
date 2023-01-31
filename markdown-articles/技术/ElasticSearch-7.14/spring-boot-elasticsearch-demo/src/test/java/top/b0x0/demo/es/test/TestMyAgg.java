package top.b0x0.demo.es.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.b0x0.demo.es.SpringBootElasticsearchDemoApplicationTests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 通过es实现 如下SQL效果：
 * SELECT model,COUNT(DISTINCT color) color_count
 * FROM cars
 * GROUP BY model
 * HAVING color_count > 1
 * ORDER BY color_count desc
 * LIMIT 2;
 *
 * @author ManJiis Created By 2022-03-08 21:44
 * @since 1.8
 */
public class TestMyAgg extends SpringBootElasticsearchDemoApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    String indexName = "cars";
    String groupAlis = "model_group";
    String group_filed = "model";
    String distinct_filed = "color";
    String distinct_count_alias = "color_count";

    @Test
    public void test_agg_step_one() throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
                .query(QueryBuilders.matchAllQuery()) //查询条件
                .aggregation(AggregationBuilders.terms(groupAlis).field(group_filed))//根据model字段进行聚合计算
//                .size(Integer.MAX_VALUE);
                .size(0);
        ;

        // {"size":0,"query":{"match_all":{"boost":1.0}},"aggregations":{"model_group":{"terms":{"field":"model.keyword","size":10,"min_doc_count":1,"shard_min_doc_count":0,"show_term_doc_count_error":false,"order":[{"_count":"desc"},{"_key":"asc"}]}}}}
        System.out.println("sourceBuilder = " + sourceBuilder);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //处理聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedStringTerms parsedStringTerms = aggregations.get(groupAlis);
        List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKey() + "   " + bucket.getDocCount());
        }
        /*
         * A   5
         * C   5
         * B   2
         * D   1
         */
    }

    /**
     * 去重 cardinality  对 color 字段去重
     */
    @Test
    public void test_agg_step_two() throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        // -------------------------------------- 根据model字段进行聚合计算
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(groupAlis).field(group_filed);

        // -------------------------------------- color 去重
        CardinalityAggregationBuilder cardinalityAggregationBuilder = AggregationBuilders.cardinality(distinct_count_alias).field(distinct_filed).precisionThreshold(100); //指定精度值
        aggregationBuilder.subAggregation(cardinalityAggregationBuilder);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
//                .query(QueryBuilders.matchAllQuery())
                .aggregation(aggregationBuilder)
                .size(0);
//                ;
        searchRequest.source(sourceBuilder);
        System.out.println("sourceBuilder = " + sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("searchResponse = " + searchResponse);

        ParsedStringTerms aggregation = searchResponse.getAggregations().get(groupAlis);
        List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();
            System.out.println("key = " + key);

            String keyAsString = bucket.getKeyAsString();
            System.out.println("keyAsString = " + keyAsString);

            long docCount = bucket.getDocCount();
            System.out.println("docCount = " + docCount);

            Cardinality cardinality = bucket.getAggregations().get(distinct_count_alias);
            long cardinalityValue = cardinality.getValue();
            System.out.println("cardinalityValue = " + cardinalityValue);

        }

    }

    /**
     * having count(color_count) > 1
     */
    @Test
    public void test_agg_step_three() throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        // -------------------------------------- 根据model字段进行聚合计算
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(groupAlis).field(group_filed);

        // -------------------------------------- color 去重
        CardinalityAggregationBuilder cardinalityAggregationBuilder = AggregationBuilders.cardinality(distinct_count_alias).field(distinct_filed).precisionThreshold(100); //指定精度值
        aggregationBuilder.subAggregation(cardinalityAggregationBuilder);

        // -------------------------------------- having count(color_count) > 1
        //(1) 声明BucketPath，用于后面的bucket筛选 value值与上面分组的别名保持一致
        Map<String, String> bucketsPathsMap = new HashMap<>(2);
        bucketsPathsMap.put("colorCount", distinct_count_alias);
        //(2) 设置脚本 params.xxx 这个xxx跟上面map设置的键名一致
        Script script = new Script("params.colorCount > 1");
        //(3) 构建bucket selector 实现having条件筛选过滤
        BucketSelectorPipelineAggregationBuilder bucketSelectorPipelineAggregationBuilder = PipelineAggregatorBuilders.bucketSelector("having", bucketsPathsMap, script);
        aggregationBuilder.subAggregation(bucketSelectorPipelineAggregationBuilder);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
//                .query(QueryBuilders.matchAllQuery())
                .aggregation(aggregationBuilder)
//                .size(Integer.MAX_VALUE);
        ;

        searchRequest.source(sourceBuilder);
        System.out.println("sourceBuilder = " + sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("searchResponse = " + searchResponse);

        ParsedStringTerms aggregation = searchResponse.getAggregations().get(groupAlis);
        List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();
            System.out.println("key = " + key);

            String keyAsString = bucket.getKeyAsString();
            System.out.println("keyAsString = " + keyAsString);

            long docCount = bucket.getDocCount();
            System.out.println("docCount = " + docCount);

            Cardinality cardinality = bucket.getAggregations().get(distinct_count_alias);
            long cardinalityValue = cardinality.getValue();
            System.out.println("cardinalityValue = " + cardinalityValue);
        }
    }
}
