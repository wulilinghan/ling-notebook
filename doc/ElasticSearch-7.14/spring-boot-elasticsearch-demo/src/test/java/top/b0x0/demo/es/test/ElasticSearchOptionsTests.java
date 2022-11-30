package top.b0x0.demo.es.test;

import top.b0x0.demo.es.SpringBootElasticsearchDemoApplicationTests;
import top.b0x0.demo.es.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;


public class ElasticSearchOptionsTests extends SpringBootElasticsearchDemoApplicationTests {


    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public ElasticSearchOptionsTests(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * save 索引一条文档 更新一条 文档
     *  save 方法当文档id 不存在时添加文档,当文档 id 存在时候更新文档
     */
    @Test
    public  void testIndex(){
        Product product = new Product();
        product.setId(2);
        product.setTitle("日本豆");
        product.setPrice(5.5);
        product.setDescription("日本豆真好吃,曾经非常爱吃!");
        elasticsearchOperations.save(product);
    }

    /**
     * 查询一条文档
     */
    @Test
    public void testSearch(){
        Product product = elasticsearchOperations.get("1", Product.class);
        System.out.println(product.getId()+ product.getTitle() + product.getPrice() + product.getDescription());
    }

    /**
     * 删除一条文档
     */
    @Test
    public void testDelete(){
        Product product = new Product();
        product.setId(1);
        elasticsearchOperations.delete(product);
    }

    /**
     * 删除所有
     */
    @Test
    public void testDeleteAll(){
        elasticsearchOperations.delete(Query.findAll(),Product.class);
    }

    /**
     * 查询所有
     */
    @Test
    public void testFindAll() throws JsonProcessingException {
        SearchHits<Product> productSearchHits = elasticsearchOperations.search(Query.findAll(), Product.class);
        System.out.println("总分数: "+productSearchHits.getMaxScore());
        System.out.println("符合条件总条数: "+productSearchHits.getTotalHits());
        for (SearchHit<Product> productSearchHit : productSearchHits) {
            System.out.println(new ObjectMapper().writeValueAsString(productSearchHit.getContent()));
        }
    }
}
