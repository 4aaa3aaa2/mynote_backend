package com.aaa.notes.model.dto.note;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//更新笔记请求
@Data
public class UpdateNoteRequest {

    /*
     * 笔记内容
     */
    @NotNull(message = "note not empty")
    @NotBlank(message = "note not empty")
    private String content;
    
}
