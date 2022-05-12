package com.zms.openzone.interact.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.zms.openzone.interact.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: zms
 * @create: 2022/1/29 13:15
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentEntity> {

    List<CommentEntity> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);
}
