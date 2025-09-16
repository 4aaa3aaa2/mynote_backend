package com.aaa.notes.model.dto.message;

import lombok.Data;

public class MessageDTO {
    private Integer messageId; //消息id
    private Long receiverId;  //接收者id
    private Long senderId;  //发送者id
    private Integer type;  //消息类型
    private Integer targetId;  //目标id
    private Integer targetType;  //目标类型
    private String content;  //消息内容
    private Boolean isRead;  //已读

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Integer getType() {
        return type;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public String getContent() {
        return content;
    }

    public Boolean getIsRead() {
        return isRead;
    }
}
