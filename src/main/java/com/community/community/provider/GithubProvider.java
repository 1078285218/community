package com.community.community.provider;

import com.alibaba.fastjson.JSON;
import com.community.community.dto.AccessTokenDTO;
import com.community.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class GithubProvider {
    //GitHub官方文档中，2. GitHub将用户重定向回您的网站   需要携带相关信息，以post请求访问https://github.com/login/oauth/access_token
    /**
     * accessTokenDTO是自己写的封装对象，把需要携带的相关信息封装成对象一起发送——代码规范
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        /**
         * 下面是用okhttp    建立post请求——可以参照okhttp官方文档
         */
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        //变量json:使用fastjson 把 对象accessTokenDTO转化为json格式
        //因为和上面的 MediaType重名，所以fastJSON的JSON需要把整个类的完整写出来
        String json = com.alibaba.fastjson.JSON.toJSONString(accessTokenDTO);

        RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    /*github 官方文档中所规定的要访问的url*/
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();
            //获取到返回值   也就是Token等信息
            try (Response response = client.newCall(request).execute()) {
                String string = response.body().string();
                /*System.out.println(string);*/
                //经过输出测试，知道其格式为：access_token=33e70bac659ed01848841e6f78f196d70e6b08a7&scope=user&token_type=bearer
                //1.先根据“&”进行拆分
                //      获取其第一段，也就是“access_token=33e70bac659ed01848841e6f78f196d70e6b08a7”
                //2.再根据“=”进行拆分
                //      获取其第二段，也就是“33e70bac659ed01848841e6f78f196d70e6b08a7”，即我们要的token值
                String[] split = string.split("&");
                String tokenstring = split[0];
                String[] split1 = tokenstring.split("=");
                String token = split1[1];
                /*System.out.println(token);*/
                return token;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    //3.使用访问令牌访问API  获取到授权用户的相关信息
    /**
     *  okhttp   get 请求                 Authorization: token OAUTH-TOKEN
     *                                    GET https://api.github.com/user
     */
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        //获取到返回值，也就是用户的信息
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();

            //这里也是用到了fastjson    把string 自动转化为   java的类对象(这里的类对象是GithubUser)      这样不需要自己一个个去set,他会自动匹配上的
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}