package com.community.community.controller;

import com.community.community.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class FileController {
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(@RequestParam("editormd-image-file") MultipartFile file, HttpSession session){

        String originalFilename = file.getOriginalFilename();
        String newFileName ="";
        String pic_path;
        // 上传图片
        //获取Tomcat服务器所在的路径
        String tomcat_path = System.getProperty( "user.dir" );
        System.out.println(tomcat_path);

        //路径设置为resource下
        pic_path = tomcat_path+"\\src\\main\\resources\\static\\images\\customer_photo\\";

        //随机生成图片名字
        newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
//        System.out.println("上传图片的路径：" + pic_path + newFileName);
        // 新图片
        File newFile = new File( pic_path+ newFileName);
        // 将内存中的数据写入磁盘
        FileDTO fileDTO = new FileDTO();
        try {
            file.transferTo(newFile);
            fileDTO.setSuccess(1);
            fileDTO.setUrl("/images/customer_photo/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileDTO;
    }
}