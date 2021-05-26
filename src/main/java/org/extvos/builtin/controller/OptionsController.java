package org.extvos.builtin.controller;

import org.extvos.builtin.config.UploadConfig;
import org.extvos.restlet.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenmc
 */
@RestController
@Api(tags = {"文件上传服务"})
public class OptionsController {
    @Autowired
    private UploadConfig uploadConfig;

    @ApiOperation(value = "文件上传选项", notes = "获取当前后台文件上传配置")
    @GetMapping("/_builtin/upload-options")
    public Result<UploadConfig> getUploadOptions() {
        return Result.data(uploadConfig).success();
    }
}
