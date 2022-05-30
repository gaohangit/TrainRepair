package com.ydzbinfo.emis.trainRepair.mobile.workprocess.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.mobile.model.RfidProcessCarPartInfo;
import com.ydzbinfo.emis.trainRepair.mobile.model.TwoWork;
import com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.IPhoneTwoWorkService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessLocation;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 作业过程 二级修控制器
 * Author: wuyuechang
 * Create Date Time: 2021/5/6 9:50
 * Update Date Time: 2021/5/6 9:50
 *
 * @see
 */
@RequestMapping("/mobileTwo")
@RestController
@Slf4j
public class PhoneTwoWorkController {

    
    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    @Autowired
    private IPhoneTwoWorkService phoneTwoWorkService;


    @ApiOperation(value = "手持机作业过程二级修车组查询")
    @GetMapping(value = "/getWorkTrainsetList", produces = "text/plain")
    @BussinessLog(value = "手持机作业过程二级修车组查询", key = "/mobileTwo/getWorkTrainsetList", type = "04")
    public String getWorkTrainsetList(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机作业过程二级修车组查询入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanID = jsonObject.getString("dayPlanID");
            String deptCode = jsonObject.getString("deptCode");
            String staffID = jsonObject.getString("staffID");

            List<String> workerIds = new ArrayList<>();
            if (Strings.isNotBlank(staffID)){
                workerIds.add(staffID);
            }

            List<TwoWork> twoWorks = phoneTwoWorkService.getWorkTrainsetList(unitCode, dayPlanID, deptCode, workerIds);
            log.trace("手持机作业过程二级修车组查询出参:{}", twoWorks);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", twoWorks);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机作业过程二级修车组查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "手持机作业过程二级修查询")
    @GetMapping(value = "/getWorkList", produces = "text/plain")
    @BussinessLog(value = "手持机作业过程二级修查询", key = "/mobileTwo/getWorkList", type = "04")
    public String getWorkList(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机作业过程二级修查询入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanID = jsonObject.getString("dayPlanID");
            String deptCode = jsonObject.getString("deptCode");
            String staffID = jsonObject.getString("staffID");
            String trainsetId = jsonObject.getString("trainsetId");

            List<String> trainsetIds = new ArrayList<>();
            if (Strings.isNotBlank(trainsetId)){
                trainsetIds.add(trainsetId);
            }

            List<String> workerIds = new ArrayList<>();
            if (Strings.isNotBlank(staffID)){
                workerIds.add(staffID);
            }

            TwoWork twoWorks = phoneTwoWorkService.getWorkList(unitCode, dayPlanID, deptCode, workerIds, trainsetIds);
            log.trace("手持机作业过程二级修查询出参:{}", twoWorks);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", twoWorks);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机作业过程二级修查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "二级修手持机保存项目信息")
    @PostMapping(value = "/setPacketItemInfo", produces = "text/plain")
    @BussinessLog(value = "二级修手持机保存项目信息", key = "/mobileTwo/setPacketItemInfo", type = "04")
    public String setPacketItemInfo(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.trace("二级修手持机保存项目信息入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            RfidProcessCarPartInfo rfidProcessCarPartInfo = jsonObject.toJavaObject(RfidProcessCarPartInfo.class);
            phoneTwoWorkService.setPacketItemInfo(rfidProcessCarPartInfo);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "二级修手持机保存项目信息失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "二级修手持机保存标签")
    @PostMapping(value = "/setRfIdInfo", produces = "text/plain")
    @BussinessLog(value = "二级修手持机保存标签", key = "/mobileTwo/setRfIdInfo", type = "04")
    public String setRfIdInfo(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("二级修手持机保存标签入参:{}", key);
            List<ProcessLocation> processLocations = JSONArray.parseArray(key, ProcessLocation.class);
            phoneTwoWorkService.setRfIdInfo(processLocations);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "二级修手持机保存标签失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "二级修项目结束")
    @PostMapping(value = "/setItemEnd", produces = "text/plain")
    @BussinessLog(value = "二级修项目结束", key = "/mobileTwo/setItemEnd", type = "04")
    public String setItemEnd(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.trace("二级修项目结束入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            RfidProcessCarPartInfo rfidProcessCarPartInfo = jsonObject.toJavaObject(RfidProcessCarPartInfo.class);
            phoneTwoWorkService.setItemEnd(rfidProcessCarPartInfo);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "二级修项目结束失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }
}
