package top.b0x0.demo.es.test;

import top.b0x0.demo.es.SpringBootElasticsearchDemoApplicationTests;
import top.b0x0.demo.es.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestHighLevClientForObject  extends SpringBootElasticsearchDemoApplicationTests {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public RestHighLevClientForObject(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }


    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(false).field("description").preTags("<span style='color:red;'>").postTags("</span>");
        sourceBuilder
                .query(QueryBuilders.termQuery("description","浣熊"))//查询所有
                .from(0)
                .size(30)
                .highlighter(highlightBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(searchResponse.getHits().getTotalHits().value);
        System.out.println(searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Product> productList = new ArrayList<>();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());//json
            Product product = new ObjectMapper().readValue(hit.getSourceAsString(), Product.class);
            //处理高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("description")){
                product.setDescription( highlightFields.get("description").fragments()[0].toString());
            }
            productList.add(product);
        }

        for (Product product : productList) {
            System.out.println(product);
        }
    }

    /**
     * 将对象放入 ES 中
     */
    @Test
    public void testIndex() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setTitle("小浣熊干吃面");
        product.setPrice(1.5);
        product.setDescription("小浣熊真好吃!");

        //录入 es 中
        IndexRequest indexRequest = new IndexRequest("products");
        indexRequest.id(product.getId().toString())
                        .source(new ObjectMapper().writeValueAsString(product), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.status());

    }


}
