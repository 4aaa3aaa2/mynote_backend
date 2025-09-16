package com.aaa.notes.model.entity;


import lombok.Data;
import java.util.Date;


/**
 * @ClassName Category
 * @Description 分类实体类
 */
@Data
public class Category {
    private Integer categoryId;  //分类id，主键
    private String name;  //分类名
    private Integer parentCategoryId;  //上级分类id，0表示当前是一级分类
    private Date createdAt;  //创建时间
    private Date updatedAt;  //更新时间

}
