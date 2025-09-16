package com.aaa.notes.model.vo.question;

import java.text.NumberFormat.Style;

import lombok.Data;

// 用于普通用户查询携带个人信息的问题 VO
@Data
public class QuestionUserVO {
    private Integer questionId;  //问题id，主键
    private String title;  //问题标题
    private Integer difficulty;  //问题难度1易2中3难
    private String examPoint;  //考点
    private Integer viewCount;  //浏览量
    private UserQuestionStatus userQuestionStatus;  //用户完成问题状态


    @Data
    public static class UserQuestionStatus {
        private boolean finished = false;  // 用户是否完成过这道题

        }
}
