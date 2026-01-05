package com.example.exam_backend.service;

import com.example.exam_backend.entity.Comment;
import com.example.exam_backend.entity.User;
import com.example.exam_backend.mapper.CommentMapper;
import com.example.exam_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    // 1. 发布评论
    public void addComment(Comment comment) {
        // 业务校验：内容不能为空
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        commentMapper.insert(comment);
    }

    // 2. 获取评论列表
    public List<Comment> getCommentsByQuestionId(Integer questionId) {
        return commentMapper.selectByQuestionId(questionId);
    }

    // 3. 点赞/点踩/举报
    // 返回值：true=成功，false=已经操作过
    public boolean performAction(Integer userId, Integer commentId, Integer type) {
        // 1. 查重
        int count = commentMapper.checkActionExists(userId, commentId, type);
        if (count > 0) {
            return false; // 已经操作过
        }

        // 2. 记录操作
        commentMapper.insertAction(userId, commentId, type);

        // 3. 更新统计
        if (type == 1) commentMapper.incrementLike(commentId);
        else if (type == 2) commentMapper.incrementDislike(commentId);
        else if (type == 3) commentMapper.incrementReport(commentId);

        return true;
    }

    // 4. 删除评论 (核心逻辑 + 事务控制)
    @Transactional(rollbackFor = Exception.class) // 🔥 事务：保证原子性
    public void deleteComment(Integer commentId, Integer userId) {
        // 1. 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        // 2. 权限校验
        User currentUser = userMapper.findById(userId);
        boolean isAuthor = comment.getUserId().equals(userId);
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());

        if (!isAuthor && !isAdmin) {
            throw new SecurityException("您无权删除他人的评论"); // 使用 SecurityException 代表权限不足
        }

        // 3. 级联删除 (先删附属表，再删主表)
        commentMapper.deleteActionsByCommentId(commentId);
        commentMapper.deleteById(commentId);
    }

    //  新增：根据用户ID获取评论列表 (用于管理员审计)
    public List<Comment> getCommentsByUserId(Integer userId) {
        return commentMapper.selectByUserId(userId);
    }
}