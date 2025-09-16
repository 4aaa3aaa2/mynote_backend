package com.aaa.notes.model.dto.comment;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


//评论查询参数
@Data
public class CommentQueryParams {

    /**
     * 笔记ID
     */
    @NotNull(message = "Id not empty")
    private Integer noteId;

    /**
     * 页码
     */
    @NotNull(message = "page not empty")
    @Min(value = 1, message = "page must>0")
    private Integer page;

    /**
     * 每页大小
     */
    @NotNull(message = "pagesize not empty")
    @Min(value = 1, message = "pagesize >0 ")
    private Integer pageSize;

}
