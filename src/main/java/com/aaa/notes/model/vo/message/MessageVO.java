package com.aaa.notes.model.vo.message;



import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息视图对象
 */
@Data
public class MessageVO {
    private Integer messageId;   //消息id
    private Sender sender;  //发送者信息
    private Integer type;  //消息类型
    private Target target;  //目标id
    private String content;  //消息内容
    private Boolean isRead;  //已读
    private LocalDateTime createdAt;  //创建时间


    /**
     * 简单用户信息
     */
    @Data
    public static class Sender {
        private Long userId;
        private String username;
        private String avatarUrl;

    }

    @Data
    public static class Target {
        private Integer targetId;
        private Integer targetType;
        private QuestionSummary questionSummary;
    }

    @Data
    public static class QuestionSummary {
        private Integer questionId;
        private String title;

    }
}
