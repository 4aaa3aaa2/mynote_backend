package com.aaa.notes.model.vo.questionList;

import lombok.Data;

@Data
public class CreateQuestionListVO {
    private Integer questionListId;

    public Integer getQuestionListId() {
        return questionListId;
    }

    public void setQuestionListId(Integer questionListId) {
        this.questionListId = questionListId;
    }
}
