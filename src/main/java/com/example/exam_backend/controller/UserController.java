package com.example.exam_backend.controller;

import com.example.exam_backend.entity.Comment;
import com.example.exam_backend.entity.User;
import com.example.exam_backend.service.CommentService;
import com.example.exam_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService; // 👈 注入 CommentService 用于查询评论

    // 1. 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User dbUser = userService.login(user.getUsername(), user.getPassword());
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("data", dbUser);
        } catch (SecurityException e) {
            result.put("code", 403); // 封号
            result.put("msg", e.getMessage());
        } catch (IllegalArgumentException e) {
            result.put("code", 400); // 账号密码错误
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "系统繁忙");
        }
        return result;
    }

    // 2. 注册接口
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.register(user);
            result.put("code", 200);
            result.put("msg", "欢迎加入 EazyExam！🚀");
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "注册失败，请稍后重试");
        }
        return result;
    }

    // 3. 管理员获取用户列表
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", userService.getUserList());
        return result;
    }

    // 4. 修改用户状态 (封号/解封)
    @PostMapping("/status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String status = (String) params.get("status");

        userService.updateUserStatus(id, status);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "状态更新成功");
        return result;
    }

    // 5. 审计接口：查看某人的所有评论
    // 这个逻辑调用 CommentService 更合适，因为返回的是 Comment 数据
    @GetMapping("/comments/{userId}")
    public Map<String, Object> getUserComments(@PathVariable Integer userId) {
        List<Comment> list = commentService.getCommentsByUserId(userId); // 👈 调用 CommentService

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }
}