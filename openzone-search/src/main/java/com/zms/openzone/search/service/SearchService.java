package com.zms.openzone.search.service;

import com.zms.openzone.search.vo.DiscussEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author: zms
 * @create: 2022/2/12 13:15
 */
@Service
public interface SearchService {
    void saveDiscussPost(DiscussEsModel discussEsModel);

    List<DiscussEsModel> searchPage(String keyWord, int pageNo, int limit) throws IOException;

    void savaBatchDiscussPostToEs(List<DiscussEsModel> models) throws IOException;

    void deleteDiscussPost(int entityId) throws IOException;
}
