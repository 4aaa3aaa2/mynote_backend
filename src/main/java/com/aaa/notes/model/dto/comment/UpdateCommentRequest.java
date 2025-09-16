package com.aaa.notes.model.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

//更新评论请求
@Data
public class UpdateCommentRequest {

    /**
     * 评论内容
     */
    @NotBlank(message = "comment not empty")
    private String content;

}
