package com.aaa.notes.service.impl;


import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.vo.upload.ImageVO;
import com.aaa.notes.service.FileService;
import com.aaa.notes.service.UploadService;
import com.aaa.notes.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadServiceImpl implements  UploadService{
    @Autowired
    FileService fileService;

    @Override
    public ApiResponse<ImageVO> uploadImage(MultipartFile file) {
        String url = fileService.uploadImage(file);
        ImageVO imageVO = new ImageVO();
        imageVO.setUrl(url);
        return ApiResponseUtil.success("上传成功", imageVO);
    }
}
