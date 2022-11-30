package top.b0x0.demo.es.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.b0x0.demo.es.SpringBootElasticsearchDemoApplicationTests;

import java.io.IOException;
import java.util.Map;

public class RestHighLevClientForDocumentSearchTests extends SpringBootElasticsearchDemoApplicationTests {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public RestHighLevClientForDocumentSearchTests(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * query            : 查询精确查询  查询计算文档得分 并根据文档得分进行返回
     * filter query     : 过滤查询  用来在大量数据中筛选出本地查询相关数据  不会计算文档得分 经常使用 filter query 结果进行缓存
     * 注意: 一旦使用 query 和 filterQuery  es 优先执行 filter Query 然后再执行 query
     */
    @Test
    public void testFilterQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
                .query(QueryBuilders.termQuery("description", "浣熊"))
                .postFilter(QueryBuilders.idsQuery().addIds("1").addIds("2").addIds("3"));//用来指定过滤条件

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("符合条件总数: " + searchResponse.getHits().getTotalHits().value);
        System.out.println("获取文档最大分数: " + searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println("id: " + hit.getId() + " source: " + hit.getSourceAsString());
        }
    }


    /**
     * 查询所有
     * 分页查询 form 起始位置   size 每页展示记录数
     * 排序 sort
     * 返回指定的字段 fetchSource  用来指定查询文档返回那些字段
     * 高亮结果  highlighter
     */
    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //创建高亮器
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(false).field("description").field("title").preTags("<span style='color:red;'>").postTags("</span>");
        sourceBuilder.query(QueryBuilders.termQuery("description", "熊猫"))
                .from(0) //起始位置 start = (page-1)*size
                .size(10)//每页显示条数 默认返回 10条
                .sort("price", SortOrder.ASC)//指定排序字段 参数 1: 根据哪个字段排序  参数 2:排序方式
                .fetchSource(new String[]{}, new String[]{"created_at"})//参数 1: 包含字段数组  参数 2:排除字段数组
                .highlighter(highlightBuilder);//高亮搜索结果
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("符合条件总数: " + searchResponse.getHits().getTotalHits().value);
        System.out.println("获取文档最大分数: " + searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println("id: " + hit.getId() + " source: " + hit.getSourceAsString());
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("description")) {
                System.out.println("description高亮结果: " + highlightFields.get("description").fragments()[0]);
            }
            if (highlightFields.containsKey("title")) {
                System.out.println("title高亮结果: " + highlightFields.get("title").fragments()[0]);
            }

        }
    }

    /**
     * 不同条件查询 term(关键词查询)
     */
    @Test
    public void testQuery() throws IOException {

        //1.term 关键词
        //query(QueryBuilders.termQuery("description","浣熊"));
        //2.range 范围
        //query(QueryBuilders.rangeQuery("price").gt(0).lte(1.66));
        //3.prefix 前缀
        //query(QueryBuilders.prefixQuery("title","小浣熊干"));
        //4.wildcard 通配符查询 ? 一个字符  * 任意多个字符
        //query(QueryBuilders.wildcardQuery("title","小浣熊*"));
        //5.ids 多个指定 id 查询
        //query(QueryBuilders.idsQuery().addIds("1").addIds("2"));

        //6.multi_match 多字段查询
        query(QueryBuilders.multiMatchQuery("非常不错日本豆", "title", "description"));
    }

    public void query(QueryBuilder queryBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);//指定查询条件
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("符合条件总数: " + searchResponse.getHits().getTotalHits().value);
        System.out.println("获取文档最大分数: " + searchResponse.getHits().getMaxScore());

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println("id: " + hit.getId() + " source: " + hit.getSourceAsString());
        }
    }

    /**
     * 查询所有matchAllQuery
     */
    @Test
    public void testMatchAll() throws IOException {

        SearchRequest searchRequest = new SearchRequest("products"); //指定搜索索引
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//指定条件对象
        sourceBuilder.query(QueryBuilders.matchAllQuery());//查询所有
        searchRequest.source(sourceBuilder);//指定查询条件

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);//参数 1:搜索请求对象 参数2: 请求配置对象 返回值:查询结果对象

        System.out.println("总条数: " + searchResponse.getHits().getTotalHits().value);
        System.out.println("最大得分: " + searchResponse.getHits().getMaxScore());

        //获取结果
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            System.out.println("id: " + id + " source: " + hit.getSourceAsString());
        }

    }
}
