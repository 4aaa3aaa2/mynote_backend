package com.aaa.notes.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论点赞实体类
 */
@Data
public class CommentLike {
    private Integer commentLikeId;  //点赞id
    private Integer commentId;  //评论id
    private Long userId;  //用户id
    private LocalDateTime createdAt;  //创建时间
}
