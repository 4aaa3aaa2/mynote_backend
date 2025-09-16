package com.aaa.notes.model.entity;

import lombok.Data;
import java.util.Date;

/**
 * 收藏夹实体类
 */

@Data
public class Collection {
    private Integer collectionId;  //收藏夹id，主键
    private String name;  //收藏夹名称
    private String description;  //收藏夹描述
    private Long creatorId;  //创建者id
    private Date createdAt;  //创建时间
    private Date updatedAt;  //更新时间


}
