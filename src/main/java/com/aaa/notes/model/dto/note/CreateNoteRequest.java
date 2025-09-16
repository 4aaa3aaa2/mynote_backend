package com.aaa.notes.model.dto.note;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


//发布笔记请求dto
@Data
public class CreateNoteRequest {
    /*
     * 问题ID
     */
    @NotNull(message = "id not empty")
    @Min(value = 1, message = "id must be integer")
    private Integer questionId;


    /*
     * 笔记内容
     */
    @NotBlank(message = "note not empty")
    @NotNull(message = "note not empty")
    private String content;
}
