package com.example.exam_backend.mapper;

import com.example.exam_backend.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {

    // 单个参数 (对象)，不需要 @Param，MyBatis 会自动找里面的属性
    @Insert("INSERT INTO sys_comment(user_id, question_id, content) VALUES(#{userId}, #{questionId}, #{content})")
    int insert(Comment comment);

    // 单个参数，不需要 @Param
    @Select("SELECT c.*, u.username " +
            "FROM sys_comment c " +
            "LEFT JOIN sys_user u ON c.user_id = u.id " +
            "WHERE c.question_id = #{questionId} " +
            "ORDER BY c.like_count DESC, c.create_time DESC")
    List<Comment> selectByQuestionId(Integer questionId);

    @Select("SELECT COUNT(*) FROM sys_comment_action WHERE user_id=#{userId} AND comment_id=#{commentId} AND action_type=#{type}")
    int checkActionExists(@Param("userId") Integer userId,
                          @Param("commentId") Integer commentId,
                          @Param("type") int type);
    @Insert("INSERT INTO sys_comment_action(user_id, comment_id, action_type) VALUES(#{userId}, #{commentId}, #{type})")
    void insertAction(@Param("userId") Integer userId,
                      @Param("commentId") Integer commentId,
                      @Param("type") int type);

    @Update("UPDATE sys_comment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLike(Integer id);

    @Update("UPDATE sys_comment SET dislike_count = dislike_count + 1 WHERE id = #{id}")
    void incrementDislike(Integer id);

    @Update("UPDATE sys_comment SET report_count = report_count + 1 WHERE id = #{id}")
    void incrementReport(Integer id);

    @Select("SELECT * FROM sys_comment WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Comment> selectByUserId(Integer userId);
}