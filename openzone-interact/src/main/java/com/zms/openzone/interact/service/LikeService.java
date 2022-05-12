package com.zms.openzone.interact.service;

import org.springframework.stereotype.Service;

/**
 * @author: zms
 * @create: 2022/2/7 20:09
 */
@Service
public interface LikeService {
    void like(int userId, int entityType, int entityId, int entityUserId,int postId);

    long findEntityLikeCount(int entityType, int entityId);

    Integer findEntityLikeStatus(int userId, int entityType, int entityId);

    Integer findUserLikeCount(int userId);
}
