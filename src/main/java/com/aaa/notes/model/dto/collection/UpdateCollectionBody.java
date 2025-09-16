package com.aaa.notes.model.dto.collection;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class UpdateCollectionBody {

    @Min(value = 1, message = "noteId must be integer")
    private Integer noteId;

    private UpdateItem[] collections;

    public static class UpdateItem {
        @Min(value = 1, message = "collectionId must be integer")
        private Integer collectionId;
        // 必须为 create 或者 delete
        @NotNull(message = "action not empty")
        @NotEmpty(message = "action not empty")
        @Pattern(regexp = "create|delete", message = "action must be create or delete")
        private String action;

        public Integer getCollectionId() {
            return collectionId;
        }

        public String getAction() {
            return action;
        }
    }
}
