package com.aaa.notes.model.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateCategoryBody {

    @NotBlank(message = "name not empty")
    @NotNull(message = "name not empty")
    @Length(max = 32, min = 1, message = "name length 1~32")
    private String name;


}
