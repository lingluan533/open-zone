package com.zms.openzone.interact.service;


import com.zms.openzone.interact.vo.FollowVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: zms
 * @create: 2022/2/8 15:15
 */
@Service
public interface FollowService {
    int hasFollowed(int userId1, int entityType, int userId2);

    void follow(int id, int entityType, int entityId);

    void unfollow(int id, int entityType, int entityId);

    long findFolloweeCount(int userId, int code);

    long findFollowerCount(int userId, int code);

    List<FollowVO> findFolloweesByUserId(int entityType, int userId, int offset, int limit);

    List<FollowVO> findFollowersByUserId(int entityType, int userId, int offset, int limit);
}
