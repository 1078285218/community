package com.community.community.controller;

import com.community.community.dto.CommentDTO;
import com.community.community.dto.QuestionDTO;
import com.community.community.enums.CommentTypeEnum;
import com.community.community.service.CommentService;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    /*查看发布详情*/
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model){
        /*获取内容*/
        QuestionDTO questionDTO = questionService.getById(id);
        /*增加阅读数*/
        questionService.increaseView(id);
        model.addAttribute("question",questionDTO);

        /*获取评论*/
        List<CommentDTO> comments = commentService.listByQuestionOrCommentId(id, CommentTypeEnum.QUESTION.getType());
        model.addAttribute("comments",comments);

        /*获取相关问题，也就是查询相同的Tag问题有那些*/
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        model.addAttribute("relatedQuestions",relatedQuestions);

        return "question";
    }
}