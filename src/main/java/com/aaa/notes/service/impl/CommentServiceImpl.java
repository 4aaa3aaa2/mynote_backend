package com.aaa.notes.service.impl;

import com.aaa.notes.annotation.NeedLogin;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.mapper.CommentMapper;
import com.aaa.notes.mapper.NoteMapper;
import com.aaa.notes.mapper.UserMapper;
import com.aaa.notes.mapper.CommentLikeMapper;
import com.aaa.notes.model.base.Pagination;
import com.aaa.notes.model.dto.message.MessageDTO;
import com.aaa.notes.model.entity.Collection;
import com.aaa.notes.model.entity.Comment;
import com.aaa.notes.model.entity.CommentLike;
import com.aaa.notes.model.entity.Note;
import com.aaa.notes.model.entity.User;
import com.aaa.notes.model.dto.comment.CommentQueryParams;
import com.aaa.notes.model.dto.comment.CreateCommentRequest;
import com.aaa.notes.model.dto.comment.UpdateCommentRequest;
import com.aaa.notes.model.enums.message.MessageTargetType;
import com.aaa.notes.model.enums.message.MessageType;
import com.aaa.notes.model.vo.comment.CommentVO;
import com.aaa.notes.model.vo.user.UserActionVO;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.service.CommentService;
import com.aaa.notes.service.MessageService;
import com.aaa.notes.utils.ApiResponseUtil;
import com.aaa.notes.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final MessageService messageService;
    private final RequestScopeData requestScopeData;

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<Integer> createComment(CreateCommentRequest request) {
        log.info("start to comment: request = {}", request);
        try {
            //获取用户id
            Long userId = requestScopeData.getUserId();
            //获取笔记信息
            Note note = noteMapper.findById(request.getNoteId());

            //笔记为空，报错
            if (note == null) {
                log.error("no note: noteId={}", request.getNoteId());
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "no note");
            }

            //创建评论
            Comment comment = new Comment();
            comment.setNoteId(request.getNoteId());
            comment.setContent(request.getContent());
            comment.setAuthorId(userId);
            comment.setParentId(request.getParentId());
            comment.setLikeCount(0);
            comment.setReplyCount(0);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());

            commentMapper.insert(comment);
            log.info("created a comment: commentId={}", comment.getCommentId());

            //增加笔记评论数
            noteMapper.incrementCommentCount(request.getNoteId());

            //检查是否回复上一级评论，如果是，增加母评论回复数
            if (request.getParentId() != null) {
                commentMapper.incrementReplyCount(request.getParentId());
            }

            //发送评论通知
            MessageDTO messageDTO = new MessageDTO();

            messageDTO.setType(MessageType.COMMENT);
            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(request.getNoteId());
            messageDTO.setReceiverId(note.getAuthorId());
            messageDTO.setSenderId(userId);
            messageDTO.setContent(request.getContent());
            messageDTO.setIsRead(false);

            messageService.createMessage(messageDTO);

            return ApiResponse.success(comment.getCommentId());
        } catch (Exception e) {
            log.error("failed to comment", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to comment" + e.getMessage());
        }
    }

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> updateComment(Integer commentId, UpdateCommentRequest request) {
        //获取当前用户id
        Long userId = requestScopeData.getUserId();

        //查找评论
        Comment comment = commentMapper.findById(commentId);
        //评论不存在，报错
        if (comment == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "comment not exists");
        }
        //非本人创建的评论不能修改
        if (!comment.getAuthorId().equals(userId)) {
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "unauthorized to update");
        }
        try {
            //更新评论
            comment.setContent(request.getContent());
            comment.setUpdatedAt(LocalDateTime.now());
            commentMapper.update(comment);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("failed to update comment", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to update comment");
        }
    }

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> deleteComment(Integer commentId) {
        //获取当前用户id
        Long userId = requestScopeData.getUserId();
        //根据id查找评论
        Comment comment = commentMapper.findById(commentId);
        //评论不存在或者非本人创建，报错
        if (comment == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "comment not exists");
        }
        if (!comment.getAuthorId().equals(userId)) {
            return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "unauthorized to delete");
        }

        //删除评论
        try {
            commentMapper.deleteById(commentId);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("failed to delete", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to delete");
        }
    }

    @Override
    public ApiResponse<List<CommentVO>> getComments(CommentQueryParams params) {
        try {
            //拉取整个评论树
            List<Comment> comments = commentMapper.findByNoteId(params.getNoteId());
            System.out.println(comments);

            if (CollectionUtils.isEmpty(comments)) {
                return ApiResponse.success(Collections.emptyList());
            }

            /* ---------- 数据准备：分组 + 批量查询 ---------- */
            // 2.1 一级评论列表
            List<Comment> firstLevel = comments.stream()
                    .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                    .sorted(Comparator.comparing(Comment::getCreatedAt))      // 按时间升序
                    .toList();

            // 2.2 分页
            int from = PaginationUtils.calculateOffset(params.getPage(), params.getPageSize());
            if (from >= firstLevel.size()) {
                return ApiResponse.success(Collections.emptyList());// 页码溢出，直接返回空
            }

            int to = Math.min(from + params.getPageSize(), firstLevel.size());
            List<Comment> pagedFirst = firstLevel.subList(from, to);
            // 2.3 母评论和子评论映射
            Map<Integer, List<Comment>> repliesMap = comments.stream().filter(c -> c.getParentId() != null)
                    .collect((Collectors.groupingBy(Comment::getParentId)));

            // 2.4 批量获取作者信息
            List<Long> authorIds = comments.stream().map(Comment::getAuthorId).collect(Collectors.toList());

            Map<Long, User> authorMap = userMapper.findByIdBatch(authorIds).stream().collect(Collectors.toMap(User::getUserId, u -> u));

            Long currentUserId = requestScopeData.getUserId();
            Set<Integer> likedSet;
            if (currentUserId != null) {
                List<Integer> allCommentIds = comments.stream()
                        .map(Comment::getCommentId)
                        .toList();
                likedSet = new HashSet<>(commentLikeMapper.findUserLikedCommentIds(currentUserId, allCommentIds));
            } else {
                likedSet = Collections.emptySet();
            }

            /* ---------- 递归装配 VO ---------- */
            List<CommentVO> result = pagedFirst.stream().map(c -> toVO(c, repliesMap, authorMap, likedSet)).toList();

            Pagination pagination = new Pagination(params.getPage(), params.getPageSize(), firstLevel.size());
            return ApiResponseUtil.success("", result, pagination);
        } catch (Exception e) {
            log.error("failed to get comments", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to get comments");
        }
    }

    /**
     * 把 Comment 递归转换成 CommentVO
     */
    private CommentVO toVO(Comment c,
                           Map<Integer, List<Comment>> repliesMap,
                           Map<Long, User> authorMap,
                           Set<Integer> likedSet) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(c.getCommentId());
        vo.setNoteId(c.getNoteId());
        vo.setContent(c.getContent());
        vo.setLikeCount(c.getLikeCount());
        vo.setReplyCount(c.getReplyCount());
        vo.setCreatedAt(c.getCreatedAt());
        vo.setUpdatedAt(c.getUpdatedAt());

        // 作者信息
        User author = authorMap.get(c.getAuthorId());
        if (author != null) {
            CommentVO.SimpleAuthorVO a = new CommentVO.SimpleAuthorVO();
            a.setUserId(author.getUserId());
            a.setUsername(author.getUsername());
            a.setAvatarUrl(author.getAvatarUrl());
            vo.setAuthor(a);
        }

        // 当前用户动作
        if (!likedSet.isEmpty()) {
            UserActionVO actions = new UserActionVO();
            actions.setIsLiked(likedSet.contains(c.getCommentId()));
            vo.setUserActions(actions);
        } else {
            vo.setUserActions(new UserActionVO());
            vo.getUserActions().setIsLiked(false);
        }

        // 递归子评论
        List<Comment> children = repliesMap.get(c.getCommentId());
        if (children != null && !children.isEmpty()) {
            List<CommentVO> childVOs = children.stream()
                    .map(child -> toVO(child, repliesMap, authorMap, likedSet))
                    .toList();
            vo.setReplies(childVOs);
        } else {
            vo.setReplies(Collections.emptyList());
        }
        return vo;
    }

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> likeComment(Integer commentId){
        //获取当前用户id
        Long userId = requestScopeData.getUserId();
        System.out.println(userId+"liked"+commentId);

        //获取对应评论
        Comment comment = commentMapper.findById(commentId);
        if (comment == null){
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "comment doesn't exist");
        }

        try {
            // 增加评论点赞数
            commentMapper.incrementLikeCount(commentId);
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);

            commentLikeMapper.insert(commentLike);

            //发送点赞信息
            MessageDTO messageDTO = new MessageDTO();

            messageDTO.setType(MessageType.LIKE);
            messageDTO.setReceiverId(comment.getAuthorId());
            messageDTO.setSenderId(userId);
            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(comment.getNoteId());
            messageDTO.setIsRead(false);
            messageService.createMessage(messageDTO);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("failed to like the comment", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to like the comment");
        }

    }

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> unlikeComment(Integer commentId) {
        Long userId = requestScopeData.getUserId();

        // 查询评论
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "comment doesn't exist");
        }

        try {
            // 减少评论点赞数
            commentMapper.decrementLikeCount(commentId);
            commentLikeMapper.delete(commentId, userId);
            return ApiResponse.success(new EmptyVO());
        } catch (Exception e) {
            log.error("failed to unlike the comment", e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to unlike the comment");
        }
    }
}

