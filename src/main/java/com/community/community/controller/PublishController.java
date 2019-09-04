package com.community.community.controller;

import com.community.community.cache.TagCache;
import com.community.community.mapper.QuestionMapper;
import com.community.community.model.Question;
import com.community.community.model.User;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags",TagCache.get());
        return "publish";
    }

    /*发布*/
    @PostMapping("/publish")
    public String doPublish(String title,
                            String description,
                            String tag,
                            HttpServletRequest request,
                            Model model){
        if(title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            model.addAttribute("tags",TagCache.get());
            model.addAttribute("description",description);
            model.addAttribute("tag",tag);
            return "publish";
        }
        if(description == null || description == ""){
            model.addAttribute("error","描述不能为空");
            model.addAttribute("tags",TagCache.get());
            model.addAttribute("title",title);
            model.addAttribute("tag",tag);
            return "publish";
        }
        if(tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            model.addAttribute("tags",TagCache.get());
            model.addAttribute("title",title);
            model.addAttribute("description",tag);
            return "publish";
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        question.setCommentCount(0);
        question.setLikeCount(0);
        question.setViewCount(0);
        //加入发布的人
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            model.addAttribute("error","用户未登录");
            return "/publish";
        }

        question.setCreator(user.getId().longValue());
        questionMapper.insert(question);
        /*questionMapper.create(question);*/
        return "redirect:/";
    }

    /*跳转到修改页面-----编辑*/
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Long id,
                       Model model){
        Question question = questionMapper.selectByPrimaryKey(id);
        /*Question question = questionMapper.getById(id);*/
        model.addAttribute("tags",TagCache.get());
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",id);

        return "publish";
    }
    /*更新发布*/
    @PutMapping("/publish")
    public String UpdatePublish(String id,
                                String title,
                                String description,
                                String tag,
                                HttpServletRequest request,
                                Model model){
        Long idd = Long.parseLong(id);
        Question question = new Question();
        question.setId(idd);
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setGmtModified(System.currentTimeMillis());
        questionMapper.updateByPrimaryKeySelective(question);
        /*questionMapper.update(question);*/
        return "redirect:/";
    }
}
