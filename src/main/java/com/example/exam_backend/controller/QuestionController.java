package com.example.exam_backend.controller;

import com.example.exam_backend.entity.Question;
import com.example.exam_backend.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question") // 👈 接口前缀
@CrossOrigin(origins = "*")      // 👈 允许跨域
public class QuestionController {

    @Autowired
    private QuestionMapper questionMapper;

    // 获取题目列表接口
    // GET http://localhost:8080/api/question/list
    @GetMapping("/list")
    public Map<String, Object> getQuestionList() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Question> list = questionMapper.findAll();

            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", list); // 把查到的 List<Question> 给前端
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "获取题目失败: " + e.getMessage());
        }

        return result;
    }
}