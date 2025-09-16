package com.aaa.notes.model.vo.questionListItem;


import com.aaa.notes.model.dto.note.NoteQueryParams;
import com.aaa.notes.model.vo.question.BaseQuestionVO;

import lombok.Data;

@Data
public class QuestionListItemVO {
    private Integer questionListId;
    private BaseQuestionVO question;
    private Integer rank;

}
