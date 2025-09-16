package com.aaa.notes.model.dto.questionListItem;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class QuestionListItemQueryParams {
    @NotNull(message = "questionListId 不能为空")
    @Min(value = 1, message = "questionListId 必须为正整数")
    private Integer questionListId;

    @NotNull(message = "page 不能为空")
    @Min(value = 1, message = "page 必须为正整数")
    private Integer page;

    @NotNull(message = "pageSize 不能为空")
    @Range(min = 1, max = 100, message = "pageSize 必须为 1 到 100 之间的整数")
    private Integer pageSize;

    public Integer getQuestionListId() {
        return questionListId;
    }

    public void setQuestionListId(Integer questionListId) {
        this.questionListId = questionListId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}