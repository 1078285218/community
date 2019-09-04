package com.community.community.controller;

import com.community.community.dto.PaginationDTO;
import com.community.community.exception.CustomizeErrorCode;
import com.community.community.exception.CustomizeException;
import com.community.community.model.User;
import com.community.community.service.NotificationService;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    
    /*  
     * @Description 我的发布页面control
     * @Author jealousy
     * @Date 2019/8/5 
     * @Param [action, model]  
     * @Return java.lang.String  
     */

    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name ="page",defaultValue = "1") Integer page,
                          @RequestParam(name ="size",defaultValue = "5") Integer size){

        User user = null;
        user = (User) request.getSession().getAttribute("user");
        if (user == null){
            throw new CustomizeException(CustomizeErrorCode.NOT_LOGIN);
        }

        /*我的发布*/
        if ("release".equals(action)){
            model.addAttribute("section","release");
            model.addAttribute("sectionName","我的发布");
            PaginationDTO allQuestionByUserId = questionService.findAllQuestionByUserId(user.getId().longValue(), page, size);
            model.addAttribute("QuestionAndPage",allQuestionByUserId);

        }else if ("reply".equals(action)){
            PaginationDTO NotificationByUserId = notificationService.findNotificationByUserId(user.getId().longValue(),page,size);
//            Long unreadCount = notificationService.unreadCount(user.getId());
            System.out.println(NotificationByUserId+"balalaheihei");
            model.addAttribute("NotificationAndPage",NotificationByUserId);
//            model.addAttribute("unreadCount",unreadCount);
            model.addAttribute("section","reply");
            model.addAttribute("sectionName","最新回复");
        }
        return "profile";
    }
}
