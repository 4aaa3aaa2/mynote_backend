package com.aaa.notes.service.impl;


import com.aaa.notes.mapper.MessageMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.model.dto.message.MessageDTO;
import com.aaa.notes.model.entity.Message;
import com.aaa.notes.model.entity.User;
import com.aaa.notes.model.enums.message.MessageType;
import com.aaa.notes.model.vo.message.MessageVO;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.service.MessageService;
import com.aaa.notes.service.UserService;
import com.aaa.notes.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 消息服务实现类
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestScopeData requestScopeData;

    @Override
    public Integer createMessage(MessageDTO messageDTO){
        try {
            Message message = new Message();
            BeanUtils.copyProperties(messageDTO, message);
            if (messageDTO.getContent() == null) {
                message.setContent(" ");
            }

            return messageMapper.insert(message);
        }
        catch (Exception e){
            throw  new RuntimeException("failed to create message"+e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<MessageVO>> getMessages(){
        Long currentUserId = requestScopeData.getUserId();
        List<Message> messages = messageMapper.selectByUserId(currentUserId);
        List<Long> senderIds = messages.stream().map(Message::getSenderId).toList();

        // 将 message 专成 messageVO
        Map<Long, User> userMap = userService.getUserMapByIds(senderIds);

        List<MessageVO> messageVOS = messages.stream().map(message->{
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(message,messageVO);

            MessageVO.Sender sender = new MessageVO.Sender();
            sender.setUserId(message.getSenderId());
            sender.setUsername(userMap.get(message.getSenderId()).getUsername());
            sender.setAvatarUrl(userMap.get(message.getSenderId()).getAvatarUrl());
            messageVO.setSender(sender);

            if (!Objects.equals(message.getType(), MessageType.SYSTEM)) {
                MessageVO.Target target = new MessageVO.Target();
                target.setTargetId(message.getTargetId());
                target.setTargetType(message.getTargetType());
                // TODO: 获取评论/点赞 对应的 note 的 question 信息

            }
            return messageVO;
        }).toList();
        return ApiResponse.success(messageVOS);
    }
    @Override
    public ApiResponse<EmptyVO> markAsRead(Integer messageId) {
        Long currentUserId = requestScopeData.getUserId();
        messageMapper.markAsRead(messageId, currentUserId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<EmptyVO> markAsReadBatch(List<Integer> messageIds) {
        Long currentUserId = requestScopeData.getUserId();
        messageMapper.markAsReadBatch(messageIds, currentUserId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<EmptyVO> markAllAsRead() {
        Long currentUserId = requestScopeData.getUserId();
        messageMapper.markAllAsRead(currentUserId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<EmptyVO> deleteMessage(Integer messageId) {
        Long currentUserId = requestScopeData.getUserId();
        messageMapper.deleteMessage(messageId, currentUserId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<Integer> getUnreadCount() {
        Long currentUserId = requestScopeData.getUserId();
        Integer count = messageMapper.countUnread(currentUserId);
        return ApiResponse.success(count);
    }

}
