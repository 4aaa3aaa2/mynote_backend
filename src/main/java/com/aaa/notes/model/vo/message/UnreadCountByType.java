package com.aaa.notes.model.vo.message;


import lombok.Data;

/**
 * 各类型未读消息数量
 */
@Data

public class UnreadCountByType {
    private String type;
    private Integer count;

}
