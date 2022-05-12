package com.zms.openzone.common.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zms
 * @create: 2022/2/10 12:05
 */

public class EventEntity {
    private String topic;
    private int userId;  //事件的触发者
    private int entityType; //事件触发涉及的实体
    private int entityId;
    private int entityUserId;
    private Map<String, Object> map = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public int getUserId() {
        return userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public EventEntity setTopic(String topic) {
        this.topic = topic;
        return this;
    }


    public EventEntity setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public EventEntity setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public EventEntity setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public EventEntity setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public EventEntity setMap(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
