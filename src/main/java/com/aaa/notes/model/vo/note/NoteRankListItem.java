package com.aaa.notes.model.vo.note;

import lombok.Data;
/*
 * 笔记排行榜数据输出
 */
@Data
public class NoteRankListItem {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer noteCount;
    private Integer rank;
}
