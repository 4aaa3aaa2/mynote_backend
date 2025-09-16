package com.aaa.notes.model.entity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
public class Comment {
    private Integer commentId;   //评论id
    private Integer noteId;  //笔记id
    private Long authorId;  //作者id
    private Integer parentId;  //母评论id
    private String content;  //评论内容
    private Integer likeCount;  //赞数
    private Integer replyCount;   //回复数
    private LocalDateTime createdAt;  //评论时间
    private LocalDateTime updatedAt;  //更新时间
}
