package com.community.community.cache;

import com.community.community.dto.TagDTO;

import java.util.*;

public class TagCache {
    public static List<TagDTO> get(){
        List<TagDTO> TagDTOS = new ArrayList<>();

        TagDTO kaifa = new TagDTO();
        kaifa.setCategoryName("开发语言");
        kaifa.setTags(Arrays.asList("javascript","php","css","html","html5","java","node.js","python","c++","c","shell"));
        TagDTOS.add(kaifa);

        TagDTO kuangjia = new TagDTO();
        kuangjia.setCategoryName("平台框架");
        kuangjia.setTags(Arrays.asList("laravel","spring","express","tornado","koa","struts"));
        TagDTOS.add(kuangjia);

        TagDTO fuwuqi = new TagDTO();
        fuwuqi.setCategoryName("服务器");
        fuwuqi.setTags(Arrays.asList("linux","docker","apache","ubuntu","负载均衡"));
        TagDTOS.add(fuwuqi);

        TagDTO database = new TagDTO();
        database.setCategoryName("数据库");
        database.setTags(Arrays.asList("mysql","redis","sql","oracle","sqlserver","mongodb"));
        TagDTOS.add(database);

        return TagDTOS;
    }
}
