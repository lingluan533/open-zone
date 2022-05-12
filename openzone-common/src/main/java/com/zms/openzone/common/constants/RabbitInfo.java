package com.zms.openzone.common.constants;

/**
 * 消息队列的交换机 队列 路由键名字
 *
 * @author: zms
 * @create: 2022/3/2 16:52
 */
public class RabbitInfo {
    public static class Interact {
        public static final String exchange = "interact-event-exchange";

        public static final String commentQueue = "comment.queue";
        public static final String commentRoutingKey = "comment";

        public static final String likeQueue = "like.queue";
        public static final String likeRoutingKey = "like";

        public static final String followQueue = "follow.queue";
        public static final String followRoutingKey = "follow";
    }
}
