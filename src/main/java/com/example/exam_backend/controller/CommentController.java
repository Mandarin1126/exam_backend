package com.example.exam_backend.controller;

import com.example.exam_backend.entity.Comment;
import com.example.exam_backend.service.CommentService; // 引入 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService; // 👈 只注入 Service，不再注入 Mapper

    // 1. 发评论接口
    @PostMapping("/add")
    public Map<String, Object> addComment(@RequestBody Comment comment) {
        Map<String, Object> result = new HashMap<>();
        try {
            commentService.addComment(comment); // 业务逻辑交给 Service
            result.put("code", 200);
            result.put("msg", "发布成功");
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("msg", e.getMessage()); // 捕获 Service 抛出的校验错误
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "系统繁忙");
        }
        return result;
    }

    // 2. 看评论接口
    @GetMapping("/list")
    public Map<String, Object> listComments(@RequestParam Integer questionId) {
        List<Comment> list = commentService.getCommentsByQuestionId(questionId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 3. 点赞/点踩/举报接口
    @PostMapping("/action")
    public Map<String, Object> action(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer commentId = (Integer) params.get("commentId");
        Integer type = (Integer) params.get("type");

        Map<String, Object> result = new HashMap<>();
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }

        try {
            boolean success = commentService.performAction(userId, commentId, type);
            if (success) {
                result.put("code", 200);
                result.put("msg", "操作成功");
            } else {
                result.put("code", 400);
                result.put("msg", "您已操作过");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "系统繁忙");
        }
        return result;
    }

    // 4. 删除评论接口
    @PostMapping("/delete")
    public Map<String, Object> deleteComment(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Integer userId = (Integer) params.get("userId");

        Map<String, Object> result = new HashMap<>();
        if (id == null || userId == null) {
            result.put("code", 400);
            result.put("msg", "参数缺失");
            return result;
        }

        try {
            commentService.deleteComment(id, userId); // 调用 Service 的事务方法

            result.put("code", 200);
            result.put("msg", "删除成功");

        } catch (SecurityException e) {
            // 捕获 Service 抛出的权限异常 (403)
            result.put("code", 403);
            result.put("msg", e.getMessage());
        } catch (RuntimeException e) {
            // 捕获“评论不存在”等运行时异常 (404/500)
            result.put("code", 500); // 简单起见统称为 500，或者你可以细分
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "系统异常");
        }
        return result;
    }
}