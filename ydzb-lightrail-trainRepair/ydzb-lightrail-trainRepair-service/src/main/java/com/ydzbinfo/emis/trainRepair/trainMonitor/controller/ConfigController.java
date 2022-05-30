package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.Config;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConfigService;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description 系统配置
 * @createDate 2021/3/3 9:52
 **/
@RestController
@RequestMapping({"config", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/config"})
public class ConfigController {

    protected static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    
    @Resource
    ConfigService configService;

    @ApiOperation(value = "获取所有系统配置")
    @GetMapping("/getConfigList")
    public Object getConfigList(){
        try{
            List<Config> configList = configService.getConfigList();
            return RestResult.success().setData(configList);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有系统配置");
        }
    }
    @ApiOperation(value = "获取系统配置")
    @GetMapping("/getConfig")
    public Object getConfig(Config config){
        try{
            Config con = configService.getConfig(config);
            return RestResult.success().setData(con);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取系统配置");
        }
    }

    @ApiOperation(value = "新增系统配置")
    @PostMapping("/addConfig")
    public Object addConfig(@RequestBody Config config){
        try{
            configService.addConfig(config);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "新增系统配置");

        }
    }
    @ApiOperation(value = "修改系统配置")
    @PostMapping("/updConfig")
    public Object updConfig(@RequestBody Config config){
        try{
            configService.updConfig(config);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "修改系统配置");

        }
    }
    @ApiOperation(value = "获取最大上传图片数量")
    @GetMapping("/getPictureUploadMax")
    public Object getPictureUploadMax(){
        try{
            Config config=configService.getUploadMax("PICTURE_UPLOAD_MAX","9");
            return RestResult.success().setData(config);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取最大上传图片数量错误");
        }
    }
    @ApiOperation(value = "获取最大上传视频数量")
    @GetMapping("/getVideoUploadMax")
    public Object getVideoUploadMax(){
        try{
            Config config=configService.getUploadMax("VIDEO_UPLOAD_MAX","3");
            return RestResult.success().setData(config);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取最大上传图片数量错误");
        }
    }
}
