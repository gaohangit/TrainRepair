package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSource;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.AggItemConfigModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAggItemConfigService;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaohan
 * @description 检修派工配置二级修
 * @createDate 2021/3/17 15:46
 * @modify 2022-02-11 by 冯帅
 **/
@RestController
@RequestMapping("aggItemConfig")
public class AggItemConfigController {
    protected static final Logger logger = LoggerFactory.getLogger(PostController.class);

        @Resource
    IAggItemConfigService iAggItemConfigService;

    @ApiOperation("二级修派工配置查询")
    @GetMapping(value = "/getAggItemConfigList")
    public Object getAggItemConfigList(AggItemConfigModel aggItemConfig,@RequestParam(value = "pageNum",defaultValue = "1")int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize) {
        try{
            List<AggItemConfigModel> resultList=new ArrayList<>();
            List<AggItemConfigModel> aggItemConfigs=iAggItemConfigService.getAggItemConfigList(aggItemConfig);
            JSONObject jsonObject=new JSONObject();
            for(int i=0;i<aggItemConfigs.size();i++){
                if((pageNum-1)*pageSize<=i&&i<pageNum*pageSize){
                    resultList.add(aggItemConfigs.get(i));
                }
            }
            jsonObject.put("aggItemConfigs",resultList);
            jsonObject.put("count",aggItemConfigs.size());
            return RestResult.success().setData(jsonObject);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有班组岗位");
        }
    }

    @ApiOperation("删除二级修派工配置")
    @PostMapping(value = "/delAggItemConfig")
    public Object delAggItemConfig(@RequestBody AggItemConfig aggItemConfig) {
        try{
            boolean delFlag = iAggItemConfigService.delAggItemConfig(aggItemConfig);
            //向kafka中推送数据
            if(SpringCloudStreamUtil.enableSendCloudData(TaskAllotConfigMqSource.class)){
                iAggItemConfigService.sendTwoDeleteData(aggItemConfig);
            }
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有班组岗位");
        }
    }

    @ApiOperation("修改二级修派工配置")
    @PostMapping(value = "/updAggItemConfig")
    public Object updAggItemConfig(@RequestBody AggItemConfigModel aggItemConfig) {
        try{
            iAggItemConfigService.updAggItemConfig(aggItemConfig);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有班组岗位");
        }
    }

    @ApiOperation("新增二级修派工配置")
    @PostMapping(value = "/addAggItemConfig")
    public Object addAggItemConfig(@RequestBody AggItemConfigModel aggItemConfig) {
        try{
            if(!ObjectUtils.isEmpty(aggItemConfig)){
                iAggItemConfigService.addAggItemConfig(aggItemConfig);
            }else{
                throw new RuntimeException("参数为空，请检查参数!");
            }
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "添加失败!");
        }
    }

}
