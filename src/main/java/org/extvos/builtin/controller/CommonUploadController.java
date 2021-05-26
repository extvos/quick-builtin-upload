package org.extvos.builtin.controller;

import org.extvos.builtin.config.UploadConfig;
import org.extvos.builtin.service.impl.DefaultStorageServiceImpl;
import org.extvos.restlet.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenmc
 */
@RestController
@Api(tags = {"文件上传服务"})
@RequestMapping("/_builtin/upload/{category:[A-Za-z0-9_-]+}")
public class CommonUploadController extends AbstractUploadController<DefaultStorageServiceImpl> {
    @Autowired
    private UploadConfig uploadConfig;

    @Override
    UploadConfig config() {
        return uploadConfig;
    }

    @Override
    DefaultStorageServiceImpl processor() {
        return new DefaultStorageServiceImpl();
    }
}
