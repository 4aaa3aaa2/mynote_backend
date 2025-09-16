package com.aaa.notes.model.vo.comment;

import com.aaa.notes.model.vo.user.UserActionVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象
 */
@Data
public class CommentVO {
    private Integer commentId;
    private Integer noteId;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SimpleAuthorVO author;
    private UserActionVO userActions;
    private List<CommentVO> replies;



    /**
     * 简单作者信息
     */
    @Data
    public static class SimpleAuthorVO {
        private Long userId;
        private String username;
        private String avatarUrl;

    }
}
