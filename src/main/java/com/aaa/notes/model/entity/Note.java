package com.aaa.notes.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName Note
 * @Description 笔记实体类
 */
@Data
public class Note {
    private Integer noteId;  //笔记id
    private Long authorId;  //作者id
    private Integer questionId;  //问题id
    private String content;  //笔记内容
    private Integer likeCount;  //点赞数
    private Integer commentCount;  //评论数
    private Integer collectCount;  //收藏数
    private LocalDateTime createdAt;  //创建时间
    private LocalDateTime updatedAt;  //更新时间

}
