package com.example.exam_backend.controller;

import com.example.exam_backend.entity.Question;
import com.example.exam_backend.service.QuestionService; // 引入 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService; // 👈 注入 Service，不再注入 Mapper

    // 获取题目列表接口（只显示已通过审核的题目）
    @GetMapping("/list")
    public Map<String, Object> getQuestionList() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用 Service 层获取已通过审核的题目
            List<Question> list = questionService.getApprovedQuestions();

            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "获取题目失败: " + e.getMessage());
        }

        return result;
    }

    // 用户上传题目接口
    @PostMapping("/upload")
    public Map<String, Object> uploadQuestion(@RequestBody Question question) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 设置默认审核状态为待审核（0）
            question.setReviewStatus(0);

            int rows = questionService.uploadQuestion(question);

            if (rows > 0) {
                result.put("code", 200);
                result.put("msg", "题目上传成功，等待管理员审核");
            } else {
                result.put("code", 500);
                result.put("msg", "题目上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "上传题目失败: " + e.getMessage());
        }

        return result;
    }

    // 管理员获取待审核题目列表
    @GetMapping("/pending")
    public Map<String, Object> getPendingQuestions() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Question> list = questionService.getPendingQuestions();

            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "获取待审核题目失败: " + e.getMessage());
        }

        return result;
    }

    // 管理员审核题目接口
    @PostMapping("/review")
    public Map<String, Object> reviewQuestion(@RequestParam Integer id, @RequestParam Integer reviewStatus) {
        Map<String, Object> result = new HashMap<>();

        try {
            // reviewStatus: 1-通过, 2-拒绝
            if (reviewStatus != 1 && reviewStatus != 2) {
                result.put("code", 400);
                result.put("msg", "审核状态参数错误");
                return result;
            }

            boolean success = questionService.reviewQuestion(id, reviewStatus);

            if (success) {
                String statusMsg = reviewStatus == 1 ? "通过" : "拒绝";
                result.put("code", 200);
                result.put("msg", "题目审核" + statusMsg + "成功");
            } else {
                result.put("code", 500);
                result.put("msg", "审核失败，题目不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "审核题目失败: " + e.getMessage());
        }

        return result;
    }

    // 查看指定用户上传的题目
    @GetMapping("/my")
    public Map<String, Object> getMyQuestions(@RequestParam Integer uploaderId) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Question> list = questionService.getQuestionsByUploaderId(uploaderId);

            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "获取我的题目失败: " + e.getMessage());
        }

        return result;
    }
}