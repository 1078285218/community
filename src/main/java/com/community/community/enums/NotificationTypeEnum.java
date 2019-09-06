package com.community.community.enums;

public enum NotificationTypeEnum {
    REPLY_QUESTION(1,"回复了问题"),
    REPLY_COMMENT(2,"回复了评论"),
    LIKE(3,"点赞")
    ;
    private int type;
    private String name;

    NotificationTypeEnum(int status, String name) {
        this.type = status;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
