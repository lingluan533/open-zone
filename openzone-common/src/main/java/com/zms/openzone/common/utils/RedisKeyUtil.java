package com.zms.openzone.common.utils;

/**
 * @author: zms
 * @create: 2022/2/7 19:58
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    //计算分数的帖子
    public static final String DISCUSSPOST_SCORE = "posttoscore";

    //构建对评论或帖子的赞的key
    //like:entity:entityType:entityId
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //构建用户获得赞的key
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //构建用户关注的实体的key
    //followee:userId:entityType  -> Zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //构建用户的粉丝的key
    //follower:userId:entityType  -> Zset(entityId,now)
    public static String getFollowerKey(int userId, int entityType) {
        return PREFIX_FOLLOWER + SPLIT + userId + SPLIT + entityType;
    }

    //登录凭证key
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户key
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
