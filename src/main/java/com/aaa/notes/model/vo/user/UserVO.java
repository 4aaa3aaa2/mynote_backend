package com.aaa.notes.model.vo.user;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前登录的用户获取别人的信息的 VO
 */
@Data
public class UserVO {

    private String username;  //用户名
    private Integer gender;  //性别
    private String avatarUrl;  //用户头像
    private String email;  //用户邮箱
    private String school;  //用户学校
    private String signature;  //用户签名
    private LocalDateTime lastLoginAt;  //最后登录时间
}
