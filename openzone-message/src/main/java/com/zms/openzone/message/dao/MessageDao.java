package com.zms.openzone.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zms.openzone.message.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author: zms
 * @create: 2022/2/5 15:38
 */
@Mapper
public interface MessageDao extends BaseMapper<MessageEntity> {

    @Select({"select count(1) as total from (select DISTINCT(conversation_id)" +
            " from message where  from_id != 1 and (from_id = #{userId} OR to_id = #{userId})" +
            " GROUP BY conversation_id) as temp;"})
    int selectConversationCount(@Param("userId") int uerId);

    @Select({"select * from message where id in (select MAX(id) " +
            " from message where  from_id != 1 " +
            "and (from_id =  #{userId} OR to_id = #{userId}) " +
            "GROUP BY conversation_id ) ORDER BY create_time DESC limit #{offset},#{limit} ;"})
    List<MessageEntity> selectConversationRecentMessage(@Param("userId") int uerId,@Param("offset") int offset, @Param("limit") int limit);


    @Select({"select from_id,to_id,content,create_time from message WHERE conversation_id = #{conversationId}  order by create_time desc limit #{offset},#{limit};"})
    List<Map<String,Object>> selectMessagesByConversationId(@Param("conversationId") String conversationId ,@Param("offset") int offset,@Param("limit") int limit);

    @Update({"update message set status = 1 where to_id = #{toId} and conversation_id = #{conversationId};"})
    void readMessageByConversationId(@Param("toId") int toId,@Param("conversationId") String conversationId);
    @Select({"SELECT * from message where to_id = #{to_id} and conversation_id = #{conversation_id} ORDER BY create_time desc limit 0,1;"})
    MessageEntity selectRecentNotice(@Param("to_id") int to_id, @Param("conversation_id") String conversation_id);
    @Select({"select * from message where to_id = #{userId} "+
            "and conversation_id = #{topic} " +
            " ORDER BY create_time DESC limit #{offset},#{limit} ;"})
    List<MessageEntity> selectMessagesByTopic(@Param("userId") int id,@Param("topic") String topicComment, @Param("offset") int offset, @Param("limit") int limit);
    @Update({"update message set status = 1 where to_id=#{userId} and conversation_id = #{topic} and status = 0"})
    void readNotice(@Param("userId") int id,@Param("topic") String topicComment);
}
