package com.aaa.notes.model.vo.question;

import lombok.Data;

@Data
public class QuestionNoteVO {

    private Integer questionId;
    private String title;
    private Integer difficulty;
    private String examPoint;
    private Integer viewCount;
    private UserNote userNote;


    @Data
    public static class UserNote {

        private boolean finished = false;
        private Integer noteId;
        private String content;

    }
}
