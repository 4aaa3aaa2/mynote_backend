package com.aaa.notes.model.entity;


import lombok.Data;
import java.util.Date;

/**
 * @ClassName NoteLike
 * @Description 笔记点赞关联实体类
 */
@Data
public class NoteLike {

    private Integer noteId;  //笔记id， 联合主键
    private Long userId;  //点赞用户id， 联合主键
    private Date createdAt;  //创建时间
    private Date updatedAt;  //更新时间
}
