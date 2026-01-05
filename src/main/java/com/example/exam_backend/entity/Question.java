package com.example.exam_backend.entity;

import lombok.Data;

@Data
public class Question {
    private Integer id;
    private String type;
    private String title;

    private String options; // 数据库取出来是 JSON 字符串，交给前端解析

    private String answer;

    private String explanation; // 对应数据库的新字段名

    // 审核状态: 0-待审核, 1-已通过, 2-已拒绝
    private Integer reviewStatus;

    // 上传者ID
    private Integer uploaderId;
}
