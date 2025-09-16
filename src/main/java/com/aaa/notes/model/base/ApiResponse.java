package com.aaa.notes.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * API响应类
 *
 * @param <T> 响应数据类型
 */

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;  //响应码
    private String message;  //响应信息
    private T data;  // 响应数据


    //构造Apiresponse类
    public ApiResponse(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data; 
    }

    //成功响应函数，有数据
    public static <T> ApiResponse<T> success(T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }


    //成功响应函数，无数据
    public static ApiResponse<EmptyVO> success(){
        return success(new EmptyVO());
    }


    //错误响应
    public static <T> ApiResponse <T> error(int code, String message){
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    //错误响应，带数据
    public static <T> ApiResponse<T> error(int code, String message, T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }    
}
