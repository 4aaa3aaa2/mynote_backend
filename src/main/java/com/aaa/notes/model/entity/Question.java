package com.aaa.notes.model.entity;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName Question
 * @Description 问题实体类
 */
@Data
public class Question {

    private Integer questionId;  //问题id，主键
    private Integer categoryId;  //问题分类id
    private String title;  //问题标题
    private Integer difficulty;  //难度1易2中3难
    private String examPoint;  //考点
    private Integer viewCount;  //浏览量
    private LocalDateTime createdAt;  //创建时间
    private LocalDateTime updatedAt;  //更新时间


}
