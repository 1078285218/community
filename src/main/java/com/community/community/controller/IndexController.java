package com.community.community.controller;

import com.community.community.dto.PaginationDTO;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name ="page",defaultValue = "1") Integer page,
                        @RequestParam(name ="size",defaultValue = "5") Integer size,
                        @RequestParam(name = "search",required = false) String search ){
        //默认第一页，一页5行

        //之后记得改用用多表级联，而不是用foreach
        //默认是第一页，

        PaginationDTO QuestionAndPage = questionService.findAllQuestion(page, size,search);

        model.addAttribute("QuestionAndPage",QuestionAndPage);
        model.addAttribute("search",search);
        return "index";
    }
}