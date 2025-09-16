package com.aaa.notes.model.dto.collection;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
public class CollectionQueryParams {
    @NotNull(message = "creator Id not empty")
    @Min(value = 1, message = "creatorId must be integer")
    private Long creatorId;

    @Min(value = 1, message = "noteId must be integer")
    private Integer noteId;

}
