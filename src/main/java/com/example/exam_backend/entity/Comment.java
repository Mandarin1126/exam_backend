package com.example.exam_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer questionId;
    private String content;
    private LocalDateTime createTime;

    // 统计字段
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer reportCount;

    // 非数据库字段（用于前端显示）
    private String username;
}