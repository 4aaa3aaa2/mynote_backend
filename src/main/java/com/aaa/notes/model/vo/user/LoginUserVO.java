package com.aaa.notes.model.vo.user;

import lombok.Data;

import java.time.LocalDate;

/**
 * LoginUserVO 是当前登录的用户，承载自己的信息的 VO
 * 而 UserVO 是当前登录的用户，获取的其他的用户的信息
 */
@Data
public class LoginUserVO {

    private Long userId;  //用户id
    private String account;  //用户账号
    private String username;  //用户名
    private Integer gender;  //用户性别
    private LocalDate birthday;  //用户生日
    private String avatarUrl;  //用户头像
    private String email;  //用户邮箱
    private String school;  //用户学校
    private String signature;  //用户签名
    private Integer isAdmin;  //是否管理员
}
