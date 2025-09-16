package com.aaa.notes.model.entity;


import lombok.Data;
import java.util.Date;

/**
 * @ClassName QuestionList
 * @Description 题单实体类
 */
@Data
public class QuestionList {
    
    private Integer questionListId;  //题单id
    private String name;  //题单名字
    private Integer type;  //题单类型
    private String description;  //描述
    private Date createdAt;  //创建时间
    private Date updatedAt;  //更新时间


}
