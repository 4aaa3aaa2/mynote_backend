package com.aaa.notes.model.vo.note;


import lombok.Data;
/*
 * 排行榜前3展示
 */
@Data
public class Top3Count {
    private Integer lastMonthTop3Count;
    private Integer thisMonthTop3Count;
}
