package com.zms.openzone.interact.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zms.openzone.interact.entity.DiscussPostEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: zms
 * @create: 2022/1/18 22:54
 */
@Mapper
public interface DiscussPostDao extends BaseMapper<DiscussPostEntity> {
    List<DiscussPostEntity> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    //param 用于给参数起别名 ， 如果只有一个参数，并且在<if>里使用 ，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

}
