package com.aaa.notes.model.vo.question;


import lombok.Data;

@Data
public class BaseQuestionVO {

    private Integer questionId;  //问题id，主键
    private Integer categoryId;  //问题分类id
    private String title;  //问题标题
    private Integer difficulty;  //难度1易2中3难
    private String examPoint;  //考点
    private Integer viewCount;  //浏览量


}
