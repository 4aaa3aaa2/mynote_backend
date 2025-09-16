package com.aaa.notes.service;

import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.vo.upload.ImageVO;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    /**
     * 上传图片
     */
    ApiResponse<ImageVO> uploadImage(MultipartFile file);
}
