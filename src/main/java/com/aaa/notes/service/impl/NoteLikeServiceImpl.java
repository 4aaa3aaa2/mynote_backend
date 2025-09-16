package com.aaa.notes.service.impl;

import com.aaa.notes.annotation.NeedLogin;
import com.aaa.notes.mapper.NoteLikeMapper;
import com.aaa.notes.mapper.NoteMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.model.dto.message.MessageDTO;
import com.aaa.notes.model.entity.Note;
import com.aaa.notes.model.entity.NoteLike;
import com.aaa.notes.model.enums.message.MessageTargetType;
import com.aaa.notes.model.enums.message.MessageType;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.service.MessageService;
import com.aaa.notes.service.NoteLikeService;
import com.aaa.notes.utils.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NoteLikeServiceImpl implements  NoteLikeService{

    private  NoteLikeMapper noteLikeMapper;
    private  NoteMapper noteMapper;
    private  RequestScopeData requestScopeData;
    private  MessageService messageService;

    @Override
    @NeedLogin
    @Transactional
    public Set<Integer> findUserLikedNoteIds(Long userId, List<Integer> noteIds){
        List<Integer> likedIds = noteLikeMapper.findUserLikedNoteIds(userId, noteIds);
        return new HashSet<>(likedIds);
    }

    public ApiResponse<EmptyVO> likeNote(Integer noteId){
        Long userId = requestScopeData.getUserId();
        Note note = noteMapper.findById(noteId);

        if(note == null){
            return ApiResponseUtil.error("note not exists");
        }

        try{
            NoteLike noteLike = new NoteLike();
            noteLike.setNoteId(noteId);
            noteLike.setUserId(userId);
            noteLike.setCreatedAt(new Date());
            noteLikeMapper.insert(noteLike);

            noteMapper.likeNote(noteId);

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageType.LIKE);
            messageDTO.setReceiverId(note.getAuthorId());
            messageDTO.setSenderId(userId);
            messageDTO.setTargetType(MessageTargetType.NOTE);
            messageDTO.setTargetId(noteId);
            messageDTO.setIsRead(false);

            System.out.println(messageDTO);
            messageService.createMessage(messageDTO);
            return ApiResponseUtil.success("like succeeded");
        } catch (Exception e) {
            return ApiResponseUtil.error("like failed");
        }
    }


    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> unlikeNote(Integer noteId){
        Long userId = requestScopeData.getUserId();
        Note note =  noteMapper.findById(noteId);

        if(note == null){
            return ApiResponseUtil.error("note not exists");
        }
        try {
            // 删除点赞记录
            NoteLike noteLike = noteLikeMapper.findByUserIdAndNoteId(userId, noteId);
            if (noteLike != null) {
                noteLikeMapper.delete(noteLike);
                // 减少笔记点赞数
                noteMapper.unlikeNote(noteId);
            }
            return ApiResponseUtil.success("unlike succeeded");
        } catch (Exception e) {
            return ApiResponseUtil.error("unlike failed");
        }

    }
}
