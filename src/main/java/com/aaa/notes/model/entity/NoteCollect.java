package com.aaa.notes.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 笔记收藏实体类
 */
@Data
public class NoteCollect {
    
    private Integer collectId;  //收藏id
    private Integer noteId;  //笔记id
    private Long userId;  //用户id
    private LocalDateTime createdAt;  //创建时间
}
