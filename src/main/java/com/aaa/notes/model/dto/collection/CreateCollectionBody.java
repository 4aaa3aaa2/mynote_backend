package com.aaa.notes.model.dto.collection;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateCollectionBody {
    
    @NotNull(message = "name not empty")
    @NotBlank(message = "name not empty")
    private String name;
    private String description;

}
