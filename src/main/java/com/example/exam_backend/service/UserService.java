package com.example.exam_backend.service;

import com.example.exam_backend.entity.User;
import com.example.exam_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    // 注册逻辑
    public String register(User user) {
        // 1. 先查用户名是否存在
        User exist = userMapper.findByUsername(user.getUsername());
        if (exist != null) {
            return "用户名已存在";
        }
        // 2. 设置默认值
        if (user.getRole() == null) user.setRole("USER");


        userMapper.insert(user);
        return "success";
    }

    // 管理员：查列表
    public List<User> getUserList() {
        return userMapper.selectList();
    }

    // 管理员：删除
    public void deleteUser(Integer id) {
        userMapper.deleteById(id);
    }
}