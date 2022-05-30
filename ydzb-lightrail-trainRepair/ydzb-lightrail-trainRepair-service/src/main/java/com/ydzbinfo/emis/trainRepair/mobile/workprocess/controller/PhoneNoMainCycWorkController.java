package com.ydzbinfo.emis.trainRepair.mobile.workprocess.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCyc;
import com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.IPhoneNoMainCycWorkService;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 无修程作业过程
 * Author: wuyuechang
 * Create Date Time: 2021/5/13 10:29
 * Update Date Time: 2021/5/13 10:29
 *
 * @see
 */
@RequestMapping("/mobileNoMainCyc")
@RestController
@Slf4j
public class PhoneNoMainCycWorkController {

    
    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    @Autowired
    private IPhoneNoMainCycWorkService phoneNoMainCycWorkService;


    @ApiOperation(value = "手持机作业过程无修程查询")
    @GetMapping(value = "/getWorkList", produces = "text/plain")
    @BussinessLog(value = "手持机作业过程无修程查询", key = "/mobileNoMainCyc/getWorkList", type = "04")
    public String getWorkList(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机作业过程无修程查询入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanID = jsonObject.getString("dayPlanID");
            String workerType = jsonObject.getString("workerType");
            List<NoMainCyc> twoWorks = phoneNoMainCycWorkService.getWorkList(unitCode, dayPlanID, workerType);
            List<NoMainCyc> resData = new ArrayList<>();
            if(!CollectionUtils.isEmpty(twoWorks)){
                JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(twoWorks, SerializerFeature.DisableCircularReferenceDetect));
                resData = jsonArray.toJavaList(NoMainCyc.class);
            }
            log.info("手持机作业过程无修程查询出参:{}", twoWorks);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", resData);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机作业过程无修程查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "无修程保存信息")
    @PostMapping(value = "/setNoManCycInfo", produces = "text/plain")
    @BussinessLog(value = "无修程保存信息", key = "/mobileNoMainCyc/setNoManCycInfo", type = "04")
    public String setNoManCycInfo(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("无修程保存信息入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            NoMainCyc noMainCyc = jsonObject.toJavaObject(NoMainCyc.class);
            phoneNoMainCycWorkService.setNoManCycInfo(noMainCyc);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "无修程保存信息失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }
}
