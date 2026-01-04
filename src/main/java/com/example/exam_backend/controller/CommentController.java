package com.example.exam_backend.controller;

import com.example.exam_backend.entity.User;
import com.example.exam_backend.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper; // 👈 新增：我们需要查用户的角色(是否是管理员)

    // 4. 删除评论接口
    @PostMapping("/delete")
    public Map<String, Object> deleteComment(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");       // 待删除的评论ID
        Integer userId = (Integer) params.get("userId"); // 操作人的ID

        Map<String, Object> result = new HashMap<>();

        // 1. 基础参数校验
        if (id == null || userId == null) {
            result.put("code", 400);
            result.put("msg", "参数缺失");
            return result;
        }

        try {
            // 2. 🔥 核心逻辑：先查询这条评论是否存在
            Comment comment = commentMapper.selectById(id);
            if (comment == null) {
                result.put("code", 404);
                result.put("msg", "评论不存在或已被删除");
                return result;
            }

            // 3. 🔥 权限判断
            // 查出当前操作的用户信息
            User currentUser = userMapper.findById(userId);

            // 判断 A: 是评论的作者吗？
            boolean isAuthor = comment.getUserId().equals(userId);
            // 判断 B: 是管理员吗？(假设数据库role字段存的是 'ADMIN')
            boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());

            // 如果既不是作者，也不是管理员，就拒绝
            if (!isAuthor && !isAdmin) {
                result.put("code", 403); // 403 Forbidden
                result.put("msg", "您无权删除他人的评论");
                return result;
            }

            // (可选) 最好连带删除点赞记录，防止脏数据
            commentMapper.deleteActionsByCommentId(id);


            // 4. 验证通过，执行删除
            commentMapper.deleteById(id);



            result.put("code", 200);
            result.put("msg", "删除成功");

        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "系统异常: " + e.getMessage());
        }
        return result;
    }
}