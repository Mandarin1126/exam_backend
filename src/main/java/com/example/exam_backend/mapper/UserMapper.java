package com.example.exam_backend.mapper;

import java.util.List;
import com.example.exam_backend.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    // 登录查用户
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND password = #{password}")
    User login(@Param("username") String username, @Param("password") String password);

    // 注册/新增用户
    @Insert("INSERT INTO sys_user(username, password, nickname, role) VALUES(#{username}, #{password}, #{nickname}, #{role})")
    int insert(User user);

    // 检查用户名是否存在
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(String username);

    // --- 管理员用的 ---

    // 获取所有用户
    @Select("SELECT u.*, IFNULL(SUM(c.report_count), 0) as totalReportCount " +
            "FROM sys_user u " +
            "LEFT JOIN sys_comment c ON u.id = c.user_id " +
            "GROUP BY u.id " +
            "ORDER BY totalReportCount DESC") // 举报多的排前面，方便管理员抓人
    List<User> selectList();

    // 删除用户
    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);

    // 修改用户信息
    @Update("UPDATE sys_user SET nickname = #{nickname}, password = #{password}, role = #{role} WHERE id = #{id}")
    int update(User user);
}
