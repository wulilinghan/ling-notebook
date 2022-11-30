package top.b0x0.demo.es.test;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.b0x0.demo.es.SpringBootElasticsearchDemoApplicationTests;

import java.io.IOException;

public class RestHighLevClientForDocumentTests extends SpringBootElasticsearchDemoApplicationTests {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public RestHighLevClientForDocumentTests(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }





    /**
     * 基于 id 查询文档
     */
    @Test
    public void testQueryById() throws IOException {
        GetRequest getRequest = new GetRequest("products", "1");
        //参数 1: 查询请求对象 参数 2:请求配置对象  返回值: 查询响应对象
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("id: "+getResponse.getId());
        System.out.println("source: "+getResponse.getSourceAsString());
    }

    /**
     * 删除文档
     */
    @Test
    public void testDelete() throws IOException {
        //参数 1: 删除请求对象 参数 2: 请求配置对象
        DeleteRequest deleteRequest = new DeleteRequest("products", "pCz-nH0Bix9jO5b3mk-q");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }

    /**
     * 更新文档
     */
    @Test
    public void testUpdate() throws IOException {
        //参数 1: 去哪个索引中更新  参数 2: 更新文档 id
        UpdateRequest updateRequest = new UpdateRequest("products","1");
        updateRequest.doc("{\"price\":1.66}",XContentType.JSON);
        //参数 1: 更新请求对象  参数 2: 请求配置对象
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
    }

    /**
     * 索引一条文档
     */
    @Test
    public void testCreate() throws IOException {
        IndexRequest indexRequest = new IndexRequest("products");
        indexRequest
                //.id("2") //手动指定文档 id
                .source("{\"title\":\"鱼豆腐\",\"price\":2.5,\"created_at\":\"2021-12-12\",\"description\":\"鱼豆腐好吃!\"}", XContentType.JSON);//指定文档数据
        //参数 1: 索引请求对象  参数 2:请求配置对象
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.status());
    }
}
