package com.example.exam_backend.entity;

import lombok.Data;

@Data // Lombok 自动生成 Getter/Setter/ToString
public class User {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private Integer totalReportCount;
    private String status;
}