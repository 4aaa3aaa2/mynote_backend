package com.aaa.notes.service.impl;

import com.aaa.notes.annotation.NeedLogin;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.Pagination;
import com.aaa.notes.model.dto.user.LoginRequest;
import com.aaa.notes.model.dto.user.RegisterRequest;
import com.aaa.notes.model.dto.user.UpdateUserRequest;
import com.aaa.notes.model.dto.user.UserQueryParam;
import com.aaa.notes.model.entity.User;
import com.aaa.notes.mapper.UserMapper;
import com.aaa.notes.model.vo.user.AvatarVO;
import com.aaa.notes.model.vo.user.RegisterVO;
import com.aaa.notes.model.vo.user.LoginUserVO;
import com.aaa.notes.model.vo.user.UserVO;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.service.EmailService;
import com.aaa.notes.service.FileService;
import com.aaa.notes.service.UserService;
import com.aaa.notes.utils.ApiResponseUtil;
import com.aaa.notes.utils.JwtUtil;
import com.aaa.notes.utils.PaginationUtils;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserServiceImpl  implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileService fileService;

    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<RegisterVO> register(RegisterRequest request) {
        User existingUser = userMapper.findByAccount(request.getAccount());

        if (existingUser !=null){
            return  ApiResponseUtil.error("same account");
        }

        if (request.getEmail()!= null && request.getEmail().isEmpty()){
            existingUser = userMapper.findByEmail(request.getEmail());
            if (existingUser != null){
                return ApiResponseUtil.error("email has been used");
            }

            if (request.getVerifyCode()== null ||request.getVerifyCode().isEmpty()){
                return ApiResponseUtil.error("please provide verify code");
            }

            if (!emailService.checkVerificationCode(request.getEmail(), request.getVerifyCode())){
                return ApiResponseUtil.error("invalid code");
            }
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try{
            userMapper.insert(user);
            String token = jwtUtil.generateToken(user.getUserId());

            RegisterVO registerVO = new RegisterVO();
            BeanUtils.copyProperties(user,registerVO);
            userMapper.updateLastLoginAt(user.getUserId());
            return ApiResponseUtil.success("registered", registerVO, token);
        }catch (Exception e) {
            log.error("register failed", e);
            return ApiResponseUtil.error("register failed, try later");
        }
    }

    @Override
    public ApiResponse<LoginUserVO> login(LoginRequest request) {
        User user = null;
        // 根据账号或邮箱查找用户
        if (request.getAccount() != null && !request.getAccount().isEmpty()) {
            user = userMapper.findByAccount(request.getAccount());
        } else if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user = userMapper.findByEmail(request.getEmail());
        } else {
            return ApiResponseUtil.error("please provide account or email");
        }

        // 验证账号以及密码
        if (user == null) {
            return ApiResponseUtil.error("user not exist");
        }

        if (!passwordEncoder.matches(request.getPassword() ,user.getPassword())){
            return ApiResponseUtil.error("password error");
        }

        String token = jwtUtil.generateToken(user.getUserId());
        LoginUserVO userVO = new LoginUserVO();
        BeanUtils.copyProperties(user, userVO);
        // 更新登录时间
        userMapper.updateLastLoginAt(user.getUserId());

        return ApiResponseUtil.success("login succeed", userVO, token);
    }

    @Override
    public ApiResponse<LoginUserVO> whoami() {
        Long userId = requestScopeData.getUserId();
        if (userId == null) {
            return ApiResponseUtil.error("abnormal id");
        }
        try{
            User user = userMapper.findById(userId);
            if(user == null){
                return  ApiResponseUtil.error("no user");
            }

            String newToken = jwtUtil.generateToken(userId);
            if(newToken == null){
                return  ApiResponseUtil.error("system error");
            }

            LoginUserVO userVO = new LoginUserVO();
            BeanUtils.copyProperties(user,userVO);

            userMapper.updateLastLoginAt(userId);
            return  ApiResponseUtil.success("login success", userVO,newToken);
        }
        catch (Exception e) {
            return ApiResponseUtil.error("system error");
        }
    }

    @Override
    public ApiResponse<UserVO> getUserInfo(Long userId) {

        User user = userMapper.findById(userId);

        if (user == null) {
            return ApiResponseUtil.error("no user");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return ApiResponseUtil.success("got user info", userVO);
    }

    @Override
    @Transactional
    @NeedLogin
    public ApiResponse<LoginUserVO> updateUserInfo(UpdateUserRequest request) {
        Long userId = requestScopeData.getUserId();

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setUserId(userId);

        System.out.println(user);

        try {
            userMapper.update(user);
            return ApiResponseUtil.success("updated");
        } catch (Exception e) {
            return ApiResponseUtil.error("update failed");
        }
    }

    @Override
    public Map<Long, User> getUserMapByIds(List<Long> authorIds) {
        if (authorIds.isEmpty()){
            return  Collections.emptyMap();
        }

        List<User> users = userMapper.findByIdBatch(authorIds);
        return  users.stream().collect(Collectors.toMap(User::getUserId , user -> user));
    }

    @Override
    public ApiResponse<List<User>> getUserList(UserQueryParam userQueryParam) {
        // 分页数据
        int total = userMapper.countByQueryParam(userQueryParam);
        int offset = PaginationUtils.calculateOffset(userQueryParam.getPage(), userQueryParam.getPageSize());
        Pagination pagination = new Pagination(userQueryParam.getPage(), userQueryParam.getPageSize(), total);

        try {
            List<User> users = userMapper.findByQueryParam(userQueryParam, userQueryParam.getPageSize(), offset);

            return ApiResponseUtil.success("获取用户列表成功", users, pagination);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }

    }

    @Override
    public ApiResponse<AvatarVO> uploadAvatar(MultipartFile file) {
        try {
            String url = fileService.uploadImage(file);
            AvatarVO avatarVO = new AvatarVO();
            avatarVO.setUrl(url);
            return ApiResponseUtil.success("上传成功", avatarVO);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }

}