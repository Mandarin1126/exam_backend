package com.example.exam_backend.controller;

import com.example.exam_backend.entity.Comment;
import com.example.exam_backend.mapper.CommentMapper;
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
    private CommentMapper commentMapper;

    // 1. 发评论接口
    @PostMapping("/add")
    public Map<String, Object> addComment(@RequestBody Comment comment) {
        Map<String, Object> result = new HashMap<>(); // 使用 HashMap

        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "内容不能为空");
            return result;
        }
        commentMapper.insert(comment);

        result.put("code", 200);
        result.put("msg", "发布成功");
        return result;
    }

    // 2. 看评论接口
    @GetMapping("/list")
    public Map<String, Object> listComments(@RequestParam Integer questionId) {
        List<Comment> list = commentMapper.selectByQuestionId(questionId);

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

        Map<String, Object> result = new HashMap<>(); // 使用 HashMap

        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }

        int count = commentMapper.checkActionExists(userId, commentId, type);
        if (count > 0) {
            result.put("code", 400);
            result.put("msg", "您已操作过");
            return result;
        }

        try {
            commentMapper.insertAction(userId, commentId, type);
            if (type == 1) commentMapper.incrementLike(commentId);
            else if (type == 2) commentMapper.incrementDislike(commentId);
            else if (type == 3) commentMapper.incrementReport(commentId);

            result.put("code", 200);
            result.put("msg", "操作成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "系统繁忙");
        }
        return result;
    }
}