package com.zms.openzone.interact.service;


import com.zms.openzone.interact.entity.DiscussPostEntity;
import com.zms.openzone.interact.vo.DiscussDetailVo;
import com.zms.openzone.interact.vo.DiscussPostVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author: zms
 * @create: 2022/1/18 23:33
 */
@Service
public interface DiscussPostService {


    List<DiscussPostVo> findDiscussPosts(int userId, int offset, int limit, int orderMode) throws ExecutionException, InterruptedException;

    int findDiscussPostRows(int userId);


    void addDiscussPost(DiscussPostEntity postEntity);

    DiscussDetailVo findDiscussPostById(int discussPostId) throws ExecutionException, InterruptedException;

    void updatePostCommentCount(int entityId);

    List<DiscussPostEntity> findAllPosts();

    void updateType(int id, int i);


    void updateStatus(int id, int i);

    void updateScore(DiscussPostEntity post);
}
