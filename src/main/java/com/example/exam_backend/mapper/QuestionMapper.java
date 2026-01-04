package com.example.exam_backend.mapper;

import com.example.exam_backend.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}