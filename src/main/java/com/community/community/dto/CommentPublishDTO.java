package com.community.community.dto;

import lombok.Data;

@Data
public class CommentPublishDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
