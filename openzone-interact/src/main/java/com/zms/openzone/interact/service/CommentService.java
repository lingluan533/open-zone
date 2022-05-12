package com.zms.openzone.interact.service;

import com.zms.openzone.interact.entity.CommentEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author: zms
 * @create: 2022/1/29 13:13
 */
@Service
public interface CommentService {


    List<CommentEntity> selectCommentsByEntity(int code, int discussPostId, int offset, int limit);

    int insertComment(CommentEntity commentEntity, int discussPostId) throws ExecutionException, InterruptedException;


    CommentEntity selectCommentById(int entityId);
}
