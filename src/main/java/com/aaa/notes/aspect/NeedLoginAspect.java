package com.aaa.notes.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aaa.notes.annotation.NeedLogin;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.utils.ApiResponseUtil;

@Aspect
@Component
public class NeedLoginAspect {

    @Autowired
    private RequestScopeData requestScopeData;

    /**
     * 拦截任何调用NeedLogin的方法，进行登录检查
     */
    @Around("@annotation(needLogin)")
    public Object around(ProceedingJoinPoint joinPoint, NeedLogin needLogin) throws Throwable {

        /**
         * 检查是否已经登录
         */
        if (!requestScopeData.isLogin()) {
            return ApiResponseUtil.error("用户未登录");
        }

        /**
         * 检查id是否有效
         */
        if (requestScopeData.getUserId() == null) {
            return ApiResponseUtil.error("用户 ID 异常");
        }
        return joinPoint.proceed();
    }
}
