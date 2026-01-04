package com.example.exam_backend.controller;

import com.example.exam_backend.entity.User;
import com.example.exam_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    // 获取用户列表
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("data", userService.getUserList());
        return res;
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", "删除成功");
        return res;
    }
}