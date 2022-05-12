package com.zms.openzone.interact.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.zms.openzone.interact.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: zms
 * @create: 2022/1/18 23:59
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

}
