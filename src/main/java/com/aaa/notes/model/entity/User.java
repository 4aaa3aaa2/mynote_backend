package com.aaa.notes.model.entity;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @ClassName User
 * @Description 用户实体类
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class User {

    private Long userId;  //用户id，主键，分配不可修改
    private String account;  //账号，唯一，注册时自定义，不可修改，数字字母下划线
    private String username;  //用户名，可修改，中文数字字母下划线
    private String password;  //加密后的登陆密码
    private Integer gender;  //性别1男2女3保密
    private LocalDate birthday;  //生日
    private String avatarUrl;  //头像地址
    private String email;  //邮箱
    private String school;  //学校
    private String signature;  //签名
    private Integer isBanned;  //封禁状态0否1是
    private Integer isAdmin;  //管理员状态0否1是
    private LocalDateTime lastLoginAt;  //最后登录时间
    private LocalDateTime createdAt;  //创建时间
    private LocalDateTime updatedAt;  //更新时间
}
