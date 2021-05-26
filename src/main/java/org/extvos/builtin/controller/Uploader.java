package org.extvos.builtin.controller;

import org.extvos.builtin.config.UploadConfig;
import org.extvos.builtin.entity.ResumableInfo;
import org.extvos.builtin.entity.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author shenmc
 */
@Component
public class Uploader {
    @Autowired
    UploadConfig config;
    @Autowired
    Resumable resumable;

    /**
     * 分片上传
     *
     * @param category
     * @param request
     * @return
     */
    public ResumableInfo uploadResumable(String category, HttpServletRequest request) {
        return resumable.upload(category, request);
    }

    /**
     * 普通上传
     *
     * @param category
     * @param file
     * @return
     */
    public UploadFile uploadNomal(String category, MultipartFile file) {
        UploadFile uploadFile = new UploadFile();
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                //业务类型目录

                //文件类型目录
                String suffixPath = suffixName;
                //日期目录
                String datePath = new StringBuffer()
                    .append(new SimpleDateFormat("yyyy").format(new Date()))
                    .append("/")
                    .append(new SimpleDateFormat("MM").format(new Date()))
                    .append("/")
                    .append(new SimpleDateFormat("dd").format(new Date()))
                    .toString();
                String fullPath = config.getRoot() + "/" + category + "/" + suffixPath + "/" + datePath;
                String visitPath = config.getPrefix() + "/" + category + "/" + suffixPath + "/" + datePath;
                //文件名
                String fileName = UUID.randomUUID() + "." + suffixName;

                File pathFile = new File(fullPath);
                //如果文件夹不存在则创建
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                //写入磁盘
//                FileUtil.writeBytes(file.getBytes(), fullPath + "/" + fileName);
                File newFile = new File(fullPath + "/" + fileName);
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
                stream.write(bytes);
                stream.close();

                uploadFile.setFilename(originalFilename);
                uploadFile.setUrl(visitPath + "/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return uploadFile;
            }
        }
        return uploadFile;
    }
}
