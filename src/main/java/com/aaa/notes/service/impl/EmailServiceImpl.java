package com.aaa.notes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aaa.notes.model.enums.redisKey.RedisKey;
import com.aaa.notes.service.EmailService;
import com.aaa.notes.task.email.EmailTask;
import com.aaa.notes.utils.RandomCodeUtil;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.rmi.server.ExportException;
import java.sql.Struct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private  RedisTemplate<String, String>  redisTemplate;

    //@Value("${mail.verify-code.limit-expire-seconds}")
    private int limitExpireSeconds;

    @Override
    public String sendVerificationCode(String email){
        if (isVerificationCodeRateLimited(email)){
            throw new RuntimeException("failed to send code, try in 60s");
        }

        String verificationCode = RandomCodeUtil.generateNumberCode(6);

        try {
           EmailTask emailTask = new EmailTask();
           emailTask.setEmail(email);
           emailTask.setCode(verificationCode);
           emailTask.setTimestamp(System.currentTimeMillis());

            // 将邮件任务存入消息队列
            // 1. 将任务对象转成 JSON 字符串
            // 2. 将 JSON 字符串保存到 Redis 模拟的消息队列中
            String emailTaskJson = objectMapper.writeValueAsString(emailTask);
            String queueKey = RedisKey.emailTaskQueue();
            redisTemplate.opsForList().leftPush(queueKey, emailTaskJson);

            String emailLimitKey = RedisKey.registerVerificationLimitCode(email);
            redisTemplate.opsForValue().set(emailLimitKey,"1",limitExpireSeconds,TimeUnit.SECONDS);

            return verificationCode;

        } catch (Exception e){
            log.error("failed to send code",e);
            throw new RuntimeException("failed to send code, try later");

        }
    }

    @Override
    public boolean checkVerificationCode(String email, String code) {
        String redisKey = RedisKey.registerVerificationCode(email);
        String verificationCode = redisTemplate.opsForValue().get(redisKey);

        if (verificationCode != null && verificationCode.equals(code)) {
            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean isVerificationCodeRateLimited(String email) {
        String redisKey = RedisKey.registerVerificationLimitCode(email);
        return redisTemplate.opsForValue().get(redisKey) != null;
    }
}
