//package com.baizhi;
//
//import org.elasticsearch.action.search.*;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.core.TimeValue;
//import org.elasticsearch.search.Scroll;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 三种分页查询方式
// * 1. from size
// * 2. search after
// * 3. scroll
// * @author ManJiis Created By 2022-01-25 22:29
// * @since 1.8
// */
//@Component
//public class EsPageDemo {
//    @Autowired
//    @Qualifier("elasticsearchRestHighLevelClient")
//    private RestHighLevelClient client;
//
//
//    //--------------------------------------------from size
//    /*
//     * @Author 【孙瑞锴】
//     * @Description es查询并返回结果
//     * @Date 2:23 下午 2019/12/23
//     * @Param [indexOrAlias 索引名称或者是别名, searchSourceBuilder 查询条件, clazz 类型]
//     * @return com.qingzhu.crs.core.tuple.Tuple2<java.util.List<T>,java.lang.Long>
//     **/
//    public <T> Tuple2<List<T>, Long> searchWithSize(String indexOrAlias, SearchSourceBuilder searchSourceBuilder, Class<T> clazz) throws IOException {
//        String conditions = searchSourceBuilder.query().toString().replaceAll("\\s*|\t|\r|\n", "");
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices(indexOrAlias);
//        searchRequest.source(searchSourceBuilder);
//        searchRequest.preference(MD5.cryptToHexString(conditions));
//        SearchResponse response = client.search(searchRequest);
//        SearchHits hits = response.getHits();
//        response.getTook(), response.getSuccessfulShards(), response.getFailedShards());
//        List<T> sourceAsMap = Arrays.stream(hits.getHits())
//                .map(item -> {
//                    T t = GsonHelper.getGson().fromJson(item.getSourceAsString(), clazz);
//                    if (t instanceof IdSearchForEs) {
//                        IdSearchForEs search = (IdSearchForEs) t;
//                        search.setId(Long.valueOf(item.getId()));
//                    }
//                    return t;
//                })
//                .collect(Collectors.toList());
//        return Tuples.of(sourceAsMap, hits.getTotalHits());
//    }
//
//
//    //--------------------------------------------search after
//
//
//    /**
//     * search after 深度分页
//     * @param indexOrAlias
//     * @param searchSourceBuilder
//     * @param clazz
//     * @param <T>
//     * @return
//     * @throws IOException
//     */
//    public <T extends BaseSearchForEs> Tuple2<List<T>, Long> searchWithSizeAndScoreAndSearchAfter(String indexOrAlias, SearchSourceBuilder searchSourceBuilder, Class<T> clazz) throws IOException
//    {
//        String conditions = searchSourceBuilder.query().toString().replaceAll("\\s*|\t|\r|\n", "");
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices(indexOrAlias);
//        searchRequest.source(searchSourceBuilder);
//        searchRequest.preference(MD5.cryptToHexString(conditions));
//        SearchResponse response = client.search(searchRequest);
//        SearchHits hits = response.getHits();
//        List<T> sourceAsMap = Arrays.stream(hits.getHits())
//                .map(item ->
//                {
//                    T t = GsonHelper.getGson().fromJson(item.getSourceAsString(), clazz);
//                    t.setSearchAfter(Arrays.asList(item.getSortValues()));
//                    t.setScore(item.getScore());
//                    return t;
//                })
//                .collect(Collectors.toList());
//        return Tuples.of(sourceAsMap, hits.getTotalHits());
//    }
//
//    //--------------------------------------------scroll
//
//    /*
//     * @Author 【孙瑞锴】
//     * @Description 批量查询，但是不适用与搜索，仅用于批查询
//     * @Date 9:02 下午 2019/12/18
//     * @Param [indexName, typeName, searchBuilder, clazz]
//     * @return com.qingzhu.crs.core.tuple.Tuple3<java.util.List<T> 列表,java.lang.Long 总条数,java.lang.String 滚动id>
//     **/
//    public <T> Tuple3<List<T>, Long, String> scroll(String indexName, String typeName, SearchSourceBuilder searchBuilder, Class<T> clazz) throws IOException
//    {
//        // 设定滚动时间间隔,60秒,不是处理查询结果的所有文档的所需时间
//        // 游标查询的过期时间会在每次做查询的时候刷新，所以这个时间只需要足够处理当前批的结果就可以了
//        Scroll scroll = new Scroll(TimeValue.timeValueSeconds(60));
//        SearchRequest searchRequest = new SearchRequest(indexName);
//        searchRequest.types(typeName);
//        searchRequest.source(searchBuilder);
//        searchRequest.scroll(scroll);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = response.getHits();
//        List<T> sourceAsMap = Arrays.stream(hits.getHits())
//                .map(item -> GsonHelper.getGson().fromJson(item.getSourceAsString(), clazz))
//                .collect(Collectors.toList());
//        return Tuples.of(sourceAsMap, hits.getTotalHits(), response.getScrollId());
//    }
//
//    /*
//     * @Author 【孙瑞锴】
//     * @Description
//     * @Date 9:09 下午 2019/12/18
//     * @Param [indexName, typeName, scrollId, clazz]
//     * @return com.qingzhu.crs.core.tuple.Tuple3<java.lang.Boolean 是否有下一批次,java.util.List<T> 列表,java.lang.String 下一批次查询ID>
//     **/
//    public <T> Tuple3<Boolean, List<T>, String> scroll(String scrollId, Class<T> clazz) throws IOException
//    {
//        final Scroll scroll = new Scroll(TimeValue.timeValueSeconds(60));
//        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
//        scrollRequest.scroll(scroll);
//        SearchResponse response = client.scroll(scrollRequest, RequestOptions.DEFAULT);
//        SearchHits hits = response.getHits();
//        List<T> sourceAsMap = Arrays.stream(hits.getHits())
//                .map(item -> GsonHelper.getGson().fromJson(item.getSourceAsString(), clazz))
//                .collect(Collectors.toList());
//        return Tuples.of(response.getHits().getHits().length != 0, sourceAsMap, response.getScrollId());
//    }
//
//    /*
//     * @Author 【孙瑞锴】
//     * @Description 清除滚屏
//     * @Date 9:13 下午 2019/12/18
//     * @Param [scrollId]
//     * @return boolean
//     **/
//    public boolean clearScroll(String scrollId) throws IOException
//    {
//        // 清除滚屏
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        // 也可以选择setScrollIds()将多个scrollId一起使用
//        clearScrollRequest.addScrollId(scrollId);
//        ClearScrollResponse clearScrollResponse = null;
//        clearScrollResponse = client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
//
//        if (clearScrollResponse != null)
//        {
//            return clearScrollResponse.isSucceeded();
//        }
//        return false;
//    }
//}
