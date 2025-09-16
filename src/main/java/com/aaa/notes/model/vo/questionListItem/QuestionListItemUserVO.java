package com.aaa.notes.model.vo.questionListItem;

import com.aaa.notes.model.vo.question.BaseQuestionVO;

import lombok.Data;

@Data
public class QuestionListItemUserVO {
    private Integer questionListId;  //题单id，联合主键
    private BaseQuestionVO question;  //题目id，联合主键
    private Boolean userQuestionStatus;  //用户是否已完成
    private Integer rank;

    public Integer getQuestionListId() {
        return questionListId;
    }

    public void setQuestionListId(Integer questionListId) {
        this.questionListId = questionListId;
    }

    public BaseQuestionVO getQuestion() {
        return question;
    }

    public void setQuestion(BaseQuestionVO question) {
        this.question = question;
    }

    public Boolean getUserQuestionStatus() {
        return userQuestionStatus;
    }

    public void setUserQuestionStatus(Boolean userQuestionStatus) {
        this.userQuestionStatus = userQuestionStatus;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Data
    public static class UserQuestionStatus {
        private boolean finished;

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }
    }
}
