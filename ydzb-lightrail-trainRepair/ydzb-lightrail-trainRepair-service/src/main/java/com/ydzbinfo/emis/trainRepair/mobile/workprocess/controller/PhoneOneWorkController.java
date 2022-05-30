package com.ydzbinfo.emis.trainRepair.mobile.workprocess.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.mobile.model.OneWork;
import com.ydzbinfo.emis.trainRepair.mobile.model.RfidProcessCarPartInfo;
import com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.IPhoneOneWorkService;
import com.ydzbinfo.emis.trainRepair.warnmanagent.querymodel.WorkWorning;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: 作业过程 一级修控制器
 * Author: wuyuechang
 * Create Date Time: 2021/5/6 9:50
 * Update Date Time: 2021/5/6 9:50
 *
 * @see
 */
@RequestMapping("/mobileOne")
@RestController
@Slf4j
public class PhoneOneWorkController {

    
    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    @Autowired
    private IPhoneOneWorkService phoneOneWorkService;

    @ApiOperation(value = "手持机作业过程一级修车组查询")
    @GetMapping(value = "/getWorkTrainsetList", produces = "text/plain")
    @BussinessLog(value = "手持机作业过程一级修车组查询", key = "/mobileOne/getWorkTrainsetList", type = "04")
    public String getWorkTrainsetList(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机作业过程一级修车组查询入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanID = jsonObject.getString("dayPlanID");
            String deptCode = jsonObject.getString("deptCode");
            String staffID = jsonObject.getString("staffID");
            List<String> workerIds = new ArrayList<>();
            if (Strings.isNotBlank(staffID)){
                workerIds.add(staffID);
            }
            List<OneWork> oneWorks = phoneOneWorkService.getWorkTrainsetList(unitCode, dayPlanID, deptCode, workerIds);
            List<OneWork> resData = new ArrayList<>();
            if(!CollectionUtils.isEmpty(oneWorks)){
                JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(oneWorks, SerializerFeature.DisableCircularReferenceDetect));
                resData = jsonArray.toJavaList(OneWork.class);
            }
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", resData);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机作业过程一级修车组查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "手持机作业过程一级修查询")
    @GetMapping(value = "/getWorkList", produces = "text/plain")
    @BussinessLog(value = "手持机作业过程一级修查询", key = "/mobileOne/getWorkList", type = "04")
    public String getWorkList(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机作业过程一级修查询入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            List<String> roleNames = ShiroKit.getUser().getRoleNames();
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanID = jsonObject.getString("dayPlanID");
            String deptCode = jsonObject.getString("deptCode");
            String staffID = jsonObject.getString("staffID");
            String trainsetId = jsonObject.getString("trainsetId");
            String itemCode = jsonObject.getString("itemCode");

            List<String> trainsetIds = new ArrayList<>();
            if (Strings.isNotBlank(trainsetId)){
                trainsetIds.add(trainsetId);
            }

            List<String> workerIds = new ArrayList<>();
            if (Strings.isNotBlank(staffID)){
                workerIds.add(staffID);
            }

            OneWork moduleIDList = phoneOneWorkService.getWorkList(unitCode, dayPlanID, deptCode, workerIds, trainsetIds, roleNames, itemCode);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", moduleIDList);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机作业过程一级修查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "一级修手持机查询时间信息")
    @GetMapping(value = "/getRfidCardSummaryTimeInfo", produces = "text/plain")
    @BussinessLog(value = "一级修手持机查询时间信息", key = "/mobileOne/getRfidCardSummaryTimeInfo", type = "04")
    public String getRfidCardSummaryTimeInfo(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("一级修手持机查询时间信息入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String staffId = jsonObject.getString("staffId");
            String trainsetId = jsonObject.getString("trainsetId");
            String repairType = jsonObject.getString("repairType");
            String itemCode = jsonObject.getString("itemCode");
            String dayplanId = jsonObject.getString("dayplanId");
            List<RfidCardSummary> rfidCardSummaries = phoneOneWorkService.getRfidCardSummaryTimeInfo(staffId, trainsetId, repairType, itemCode, dayplanId);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", rfidCardSummaries);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "一级修手持机记录时间信息失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "一级修手持机保存标签")
    @PostMapping(value = "/setRfIdInfo", produces = "text/plain")
    @BussinessLog(value = "一级修手持机记录时间信息", key = "/mobileOne/setRfIdInfo", type = "04")
    public String setRfIdInfo(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("一级修手持机保存标签入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            RfidProcessCarPartInfo rfidProcessCarPartInfo = jsonObject.toJavaObject(RfidProcessCarPartInfo.class);
            phoneOneWorkService.setRfIdInfo(rfidProcessCarPartInfo);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "一级修手持机保存标签失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "手持机一级修开始/暂停/继续/结束")
    @PostMapping(value = "/updateTime", produces = "text/plain")
    @BussinessLog(value = "手持机一级修开始/暂停/继续/结束", key = "/mobileOne/updateTime", type = "04")
    public String updateTime(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机一级修开始/暂停/继续/结束入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            RfidProcessCarPartInfo rfidProcessCarPartInfo = jsonObject.toJavaObject(RfidProcessCarPartInfo.class);
            phoneOneWorkService.updateTime(rfidProcessCarPartInfo);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机一级修开始/暂停/继续/结束失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "上传图片")
    @PostMapping(value = "/uploadImage", produces = "text/plain")
    @BussinessLog(value = "上传图片", key = "/mobileOne/uploadImage", type = "04")
    public String uploadImage(@ApiParam(type = "com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic") @RequestParam String RWMT, MultipartHttpServletRequest multipartHttpServletRequest) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("上传图片入参:{}", key);
            ProcessPic processPic = JSONObject.toJavaObject(JSONObject.parseObject(key), ProcessPic.class);
            phoneOneWorkService.uploadImage(processPic, multipartHttpServletRequest);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "上传图片失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "删除图片")
    @GetMapping(value = "/removeImage", produces = "text/plain")
    @BussinessLog(value = "删除图片", key = "/mobileOne/removeImage", type = "04")
    public String removeImage(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("删除图片入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String picId = jsonObject.getString("picId");
            phoneOneWorkService.removeImage(picId);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "删除图片失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "手持机保存预警信息")
    @PostMapping(value = "/earlyWarning", produces = "text/plain")
    @BussinessLog(value = "手持机保存预警信息", key = "/mobileOne/earlyWarning", type = "04")
    public String earlyWarning(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            ShiroUser user = ShiroKit.getUser();
            log.info("手持机保存预警信息:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            WorkWorning workWorning = jsonObject.toJavaObject(WorkWorning.class);
            workWorning.setEffectStuffId(user.getStaffId());
            workWorning.setEffectStuffName(user.getName());
            workWorning.setEffectTime(new Date());
            phoneOneWorkService.earlyWarning(workWorning);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机保存预警信息失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }
}
