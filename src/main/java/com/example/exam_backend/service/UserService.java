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

    // 1. 登录业务
    public User login(String username, String password) {
        User user = userMapper.login(username, password);

        if (user == null) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        if ("BANNED".equals(user.getStatus())) {
            throw new SecurityException("该账号严重违规，已被永久封禁！");
        }
        return user;
    }

    // 2. 注册业务
    public void register(User user) {
        // 查重
        User exist = userMapper.findByUsername(user.getUsername());
        if (exist != null) {
            throw new IllegalArgumentException("哎呀，这个名字被抢注了 🙈");
        }
        // 设置默认值
        user.setRole("USER");
        user.setStatus("NORMAL");
        userMapper.insert(user);
    }

    // 3. 获取所有用户
    public List<User> getUserList() {
        return userMapper.selectList();
    }

    // 4. 修改用户状态
    public void updateUserStatus(Integer id, String status) {
        userMapper.updateStatus(id, status);
    }

    // 5. 根据ID查用户 (给其他Service用的辅助方法)
    public User getUserById(Integer id) {
        return userMapper.findById(id);
    }
}