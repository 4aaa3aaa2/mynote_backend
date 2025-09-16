package com.aaa.notes.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aaa.notes.service.FileService;

@Service
public class LocalFileServiceImpl implements  FileService {

    /**
     * 基础上传路径（本地存储的绝对或相对路径）
     */
    @Value("${upload.path}")
    private String uploadBasePath;

    /**
     * 返回给前端的地址前缀 (可配合CDN/Nginx等)
     */
    @Value("${upload.url-prefix}")
    private String urlPrefix;


    /**
     * 允许上传的图片后缀名（小写形式）
     */
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS
            = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");

    /**
     * 单个图片最大尺寸 (10MB)
     */
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    @Override
    public  String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("no picture upload");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("no larger than 10mb");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("invalid file name");
        }

        String lowerCaseExtension = originalFilename
                .substring(originalFilename.lastIndexOf("."))
                .toLowerCase();

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(lowerCaseExtension)) {
            throw new IllegalArgumentException(
                    "only support " + ALLOWED_IMAGE_EXTENSIONS);
        }
        return doUpload(file);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        return doUpload(file);
    }


    public String doUpload(MultipartFile file){
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")){
            throw new IllegalArgumentException("invalid file name");
        }

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        String newFileName = UUID.randomUUID()+fileExtension;

        File uploadDir = new File(uploadBasePath);
        if(!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IllegalStateException("unable to upload to the path" + uploadBasePath);
        }

        File destFile = new File(uploadDir, newFileName);
        try {
                file.transferTo(destFile);
            }
        catch (IOException e) {
                throw new IllegalStateException("upload failed"+e.getMessage(), e);
        }return  urlPrefix+"/"+newFileName;
    }




}
