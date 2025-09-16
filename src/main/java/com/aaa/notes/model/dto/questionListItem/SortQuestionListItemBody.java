package com.aaa.notes.model.dto.questionListItem;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;


//问题项目排序
@Data
public class SortQuestionListItemBody {

    @NotNull(message = "questionListId 不能为空")
    @Min(value = 1, message = "questionListId 必须为正整数")
    private Integer questionListId;

    @NotNull(message = "questionListItemIds 不能为空")
    private List<Integer> questionIds;

    public Integer getQuestionListId() {
        return questionListId;
    }

    public void setQuestionListId(Integer questionListId) {
        this.questionListId = questionListId;
    }

    public List<Integer> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Integer> questionIds) {
        this.questionIds = questionIds;
    }
}
