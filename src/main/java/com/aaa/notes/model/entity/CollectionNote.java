package com.aaa.notes.model.entity;

import lombok.Data;
import java.util.Date;

/**
 * @ClassName CollectionNote
 * @Description 收藏夹-笔记关联实体类
 */

@Data
public class CollectionNote {
    private Integer collectionId;  //收藏夹id，联合主键
    private Integer noteId;  //笔记id，联合主键
    private Date createdAt;  //创建时间
    private Date updatedAt;  //更新时间

}
