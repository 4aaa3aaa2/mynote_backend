package com.aaa.notes.model.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateCategoryBody {
    @NotBlank(message = "name not empty")
    @NotNull(message = "name not empty")
    @Length(max = 32, min = 1, message = "name length 1~32")
    private String name;

    @NotNull(message = "parentCategoryId pmust be integer")
    private Integer parentCategoryId;


}
