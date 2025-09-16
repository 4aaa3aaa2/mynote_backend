package com.aaa.notes.model.entity;


import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
public class Message {
    private Integer messageId;  //消息id
    private Long receiverId;  //接收者id
    private Long senderId;  //发送者id
    private Integer type;  //消息类型
    private Integer targetId;  //目标id
    private Integer targetType;  //目标类型
    private String content;  //消息内容
    private Boolean isRead;  //已读情况
    private LocalDateTime createdAt;  //创建时间 
    private LocalDateTime updatedAt;  //更新时间
}
