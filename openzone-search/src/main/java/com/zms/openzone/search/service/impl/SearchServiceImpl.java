package com.zms.openzone.search.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zms.openzone.common.constants.CommunityConstants;

import com.zms.openzone.search.config.ElasticSearchConfig;
import com.zms.openzone.search.feign.LikeFeignService;
import com.zms.openzone.search.service.SearchService;
import com.zms.openzone.search.utils.R;
import com.zms.openzone.search.vo.DiscussEsModel;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.common.xcontent.XContentType;

import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: zms
 * @create: 2022/2/12 13:15
 */
@Service
public class SearchServiceImpl implements SearchService {


    @Qualifier("esRestClient")
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private LikeFeignService likeFeignService;

    @Override
    public void savaBatchDiscussPostToEs(List<DiscussEsModel> models) throws IOException {
        BulkRequest bulkrequest = new BulkRequest();

        for (DiscussEsModel discussEsModel : models) {
            IndexRequest request = new IndexRequest(CommunityConstants.ES_DISCUSSPOST_INDEX);
            //指定该索引下的项的id
            request.id(discussEsModel.getId() + "");
            //指定该索引下的项的内容
            request.source(JSONObject.toJSONString(discussEsModel), XContentType.JSON);
            bulkrequest.add(request);
        }
        client.bulk(bulkrequest, ElasticSearchConfig.COMMON_OPTIONS);
    }

    //根据帖子id在es中删除
    @Override
    public void deleteDiscussPost(int entityId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(CommunityConstants.ES_DISCUSSPOST_INDEX);
        deleteRequest.id(entityId + "");
        client.delete(deleteRequest, ElasticSearchConfig.COMMON_OPTIONS);
    }

    @Override
    public void saveDiscussPost(DiscussEsModel discussEsModel) {
        //保存到ES
        //1.给ES 中建立索引 product 建立好索引关系
        //这里要手动在es中创建索引，把索引名字保存在Esconstant中作为一个常量
        //2.给es中保存这些数据
        IndexRequest request = new IndexRequest(CommunityConstants.ES_DISCUSSPOST_INDEX);
        //指定该索引下的项的id
        request.id(discussEsModel.getId() + "");
        //指定该索引下的项的内容
        request.source(JSONObject.toJSONString(discussEsModel), XContentType.JSON);
        try {
            IndexResponse response = client.index(request, ElasticSearchConfig.COMMON_OPTIONS);
            System.out.println("es插入返回状态：" + response.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //另外一种做法是：使用DiscussPostRepository，类似于MyBatis 的一套方法
    }

    @Override
    public List<DiscussEsModel> searchPage(String keyWord, int pageNo, int limit) throws IOException {
        //1.准备检索请求
        SearchRequest searchRequest = buildSearchRequest(keyWord, pageNo, limit);
        //2.执行检索
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<DiscussEsModel> posts = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            DiscussEsModel discussEsModel = JSONObject.parseObject(sourceAsString, DiscussEsModel.class);
            //设置高亮属性
            HighlightField highLightitle = hit.getHighlightFields().get("title");
            if (highLightitle != null) {
                String highLight = highLightitle.getFragments()[0].string();
                discussEsModel.setTitle(highLight);
            }
            HighlightField highLighcontent = hit.getHighlightFields().get("content");
            if (highLighcontent != null) {
                String highLight = highLighcontent.getFragments()[0].string();
                discussEsModel.setContent(highLight);
            }
            //封装点赞信息  【远程调用 interact 服务】
            R r = likeFeignService.getEntityLikeCount(CommunityConstants.LikeTypeEnum.DISCUSS.getCode(), discussEsModel.getId());
            if (r.getCode() == 0) {
                discussEsModel.setLikeCount(r.getData(new TypeReference<Long>() {
                }));
            }
            posts.add(discussEsModel);

        }
        return posts;
    }

    /*
     * 准备检索请求
     * 模糊匹配 ，排序，分页，高亮
     * */
    private SearchRequest buildSearchRequest(String keyWord, int pageNo, int limit) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//构建DSL语句的
        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(limit);
        //1.1 must-模糊匹配
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyWord, "title", "content");
        sourceBuilder.query(matchQueryBuilder)
                .sort("type", SortOrder.DESC)
                .sort("score", SortOrder.DESC)
                .sort("createTime", SortOrder.DESC);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").requireFieldMatch(false).preTags("<span style='color:red'>").postTags("</span>");
        highlightBuilder.field("content").requireFieldMatch(false).
                preTags("<span style='color:red'>").postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        SearchRequest searchRequest = new SearchRequest();
        return searchRequest.source(sourceBuilder);

    }
}
