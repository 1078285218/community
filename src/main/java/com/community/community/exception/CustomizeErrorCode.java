package com.community.community.exception;

public enum  CustomizeErrorCode {

    QUESTION_NOT_FOUND(2001,"你找的问题不在了， 换其他问题试试"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论进行回复"),
    NOT_LOGIN(2003,"当前操作需要登录，请先进行登录"),
    SYS_ERROR(2004,"服务器异常，稍后再试!"),
    COMMENT_NOT_FOUND(2005,"回复的评论不存在了~~"),
    COMMENT_IS_EMPTY(2006,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2007,"无法进行该操作，该条消息不是属于您的~"),
    NOTIFICATION_NOT_FOUND(2008,"该消息不见了~┭┮﹏┭┮"),
    File_error(2009,"文件上传失败")
            ;

    private Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
