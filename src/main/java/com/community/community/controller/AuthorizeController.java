package com.community.community.controller;

import com.community.community.dto.AccessTokenDTO;
import com.community.community.dto.GithubUser;
import com.community.community.mapper.UserMapper;
import com.community.community.model.User;
import com.community.community.provider.GithubProvider;
import com.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    //   在1.请求用户的GitHub身份之后，github会重定向会我当时在github上设置的callback路径，并返回code     我设置的是//http://localhost:8888/callback
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        /*accessTokenDTO.setRedirect_uri("http://localhost:8888/callback");
        accessTokenDTO.setClient_id("7fa9483be17ac735da20");
        accessTokenDTO.setClient_secret("9505dacc1634800285c32cb2504910cd10590a54");*/
        //可以在application.properties中进行配置好，然后调用，方便之后修改。
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(redirectUri);

        //进行第二步操作，根据获得的code去获取Token --------------------2
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        //进行第三步操作，根据获得的Token去获取User（用户）信息------------------3
        GithubUser githubUser = githubProvider.getUser(accessToken);

        if(githubUser != null){

            User user = new User();
            String token = UUID.randomUUID().toString();//自定义token
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());

            userService.CreateOrUpdate(user);

            //登录成功，写cookie和session
            response.addCookie(new Cookie("token",token));

            /*request.getSession().setAttribute("githubUser",githubUser);*/
            return "redirect:/";
        }else{
            //登录失败，重新登录
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response){
        //清除session和cookie，最好在前端实现
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";
    }
}
