package com.example.exam_backend.controller;

import com.example.exam_backend.entity.User;
import com.example.exam_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;
    // 接收 JSON 格式的登录请求
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        log.info("收到登录请求，用户名：{}，密码：{}", user.getUsername(), user.getPassword());
        Map<String, Object> result = new HashMap<>();
        // 查询数据库
        User dbUser = userService.login(user.getUsername(), user.getPassword());

        if (dbUser != null) {
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("data", dbUser); // 把用户信息返给前端
        } else {
            result.put("code", 400);
            result.put("msg", "账号或密码错误");
        }
        return result;
    }
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        String msg = userService.register(user);

        if ("success".equals(msg)) {
            result.put("code", 200);
            result.put("msg", "注册成功，请登录");
        } else {
            result.put("code", 400);
            result.put("msg", msg);
        }
        return result;
    }
}