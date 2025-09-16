package com.aaa.notes.model.entity;


import lombok.Data;

import java.time.LocalDate;

/**
 * 统计信息实体，包含登录、注册、笔记等统计数据
 */
@Data
public class Statistic {

    private Integer id;  //主键id
    private Integer loginCount;  //登录次数
    private Integer registerCount ;  //注册人数
    private Integer totalRegisterCount;  //累计注册人数
    private Integer noteCount;  //笔记数
    private Integer submitNoteCount;  //提交笔记数
    private Integer totalNoteCount;  //累计笔记数
    private LocalDate date;  //统计日期

}
