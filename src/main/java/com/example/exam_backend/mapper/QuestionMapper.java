package com.example.exam_backend.mapper;

import com.example.exam_backend.entity.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QuestionMapper {

    // 1. 获取所有题目 (最常用)
    // 注意：数据库字段是 explanation，如果你的实体类叫 explain，这里要写别名
    // 但为了简单，假设你的实体类字段名和数据库一致，或者你稍后在前端处理映射
    @Select("SELECT * FROM question")
    List<Question> findAll();

    // 2. (可选) 如果你想按科目加载题目，比如只查 'math'
    @Select("SELECT * FROM question WHERE type = #{type}")
    List<Question> findByType(String type);

    // 3. 获取所有已通过审核的题目
    @Select("SELECT * FROM question WHERE review_status = 1")
    List<Question> findApprovedQuestions();

    // 4. 获取所有待审核的题目
    @Select("SELECT * FROM question WHERE review_status = 0")
    List<Question> findPendingQuestions();

    // 5. 用户上传题目
    @Insert("INSERT INTO question (type, title, options, answer, explanation, review_status, uploader_id) " +
            "VALUES (#{type}, #{title}, #{options}, #{answer}, #{explanation}, 0, #{uploaderId})")
    int insertQuestion(Question question);

    // 6. 审核题目（通过/拒绝）
    @Update("UPDATE question SET review_status = #{reviewStatus} WHERE id = #{id}")
    int updateReviewStatus(@Param("id") Integer id, @Param("reviewStatus") Integer reviewStatus);

    // 7. 获取指定用户上传的题目
    @Select("SELECT * FROM question WHERE uploader_id = #{uploaderId}")
    List<Question> findByUploaderId(@Param("uploaderId") Integer uploaderId);
}