package com.example.exam_backend.service;

import com.example.exam_backend.entity.Question;
import com.example.exam_backend.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    // 获取所有题目
    // 将来如果你想做"随机打乱题目顺序"或者"只取前10题"，代码就写在这里
    public List<Question> getAllQuestions() {
        return questionMapper.findAll();
    }

    // 获取所有已通过审核的题目
    public List<Question> getApprovedQuestions() {
        return questionMapper.findApprovedQuestions();
    }

    // 获取所有待审核的题目（管理员用）
    public List<Question> getPendingQuestions() {
        return questionMapper.findPendingQuestions();
    }

    // 用户上传题目
    public int uploadQuestion(Question question) {
        return questionMapper.insertQuestion(question);
    }

    // 审核题目（通过/拒绝）
    public boolean reviewQuestion(Integer id, Integer reviewStatus) {
        int result = questionMapper.updateReviewStatus(id, reviewStatus);
        return result > 0;
    }

    // 获取指定用户上传的题目
    public List<Question> getQuestionsByUploaderId(Integer uploaderId) {
        return questionMapper.findByUploaderId(uploaderId);
    }
}