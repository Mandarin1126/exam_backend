package com.example.exam_backend.controller;

import com.example.exam_backend.entity.Comment;
import com.example.exam_backend.entity.User;
import com.example.exam_backend.mapper.CommentMapper;
import com.example.exam_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap; // 必须导入
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;

    // 1. 登录接口 (包含封号拦截)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        User dbUser = userMapper.login(user.getUsername(), user.getPassword());
        Map<String, Object> result = new HashMap<>(); // 改用 HashMap

        if (dbUser == null) {
            result.put("code", 400);
            result.put("msg", "账号或密码错误");
        } else if ("BANNED".equals(dbUser.getStatus())) {
            // ⛔️ 拦截已封号用户
            result.put("code", 403);
            result.put("msg", "该账号严重违规，已被永久封禁！");
        } else {
            result.put("code", 200);
            result.put("data", dbUser);
            result.put("msg", "登录成功");
        }
        return result;
    }

    // 2. 管理员获取用户列表 (包含举报统计)
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", userMapper.selectList());
        return result;
    }

    // 3. 修改用户状态 (封号/解封)
    @PostMapping("/status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String status = (String) params.get("status");

        userMapper.updateStatus(id, status);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "状态更新成功");
        return result;
    }

    // 4. 审计接口：查看某人的所有评论
    @GetMapping("/comments/{userId}")
    public Map<String, Object> getUserComments(@PathVariable Integer userId) {
        List<Comment> list = commentMapper.selectByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 5. 注册接口 (如果你需要的话)
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User exist = userMapper.findByUsername(user.getUsername());
            if (exist != null) {
                result.put("code", 400);
                result.put("msg", "用户名已存在");
                return result;
            }
            userMapper.insert(user);
            result.put("code", 200);
            result.put("msg", "注册成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "注册失败");
        }
        return result;
    }
}