package com.community.community.mapper;

import com.community.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user (name,account_id,token,gmt_create,gmt_modified) VALUES(#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    public void insert(User user);
    @Select("SELECT * FROM user WHERE token = #{token} ")
    public User finByToken(@RequestParam("token") String token);
}