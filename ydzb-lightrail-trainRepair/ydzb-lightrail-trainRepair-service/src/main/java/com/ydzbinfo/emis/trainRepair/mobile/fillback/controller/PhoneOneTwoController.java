package com.ydzbinfo.emis.trainRepair.mobile.fillback.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.OneTwoRepairCheckList;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.QueryCheckListSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneChecklistSummaryInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneChecklistSummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneOneTwoRepairCheck;
import com.ydzbinfo.emis.trainRepair.mobile.fillback.service.IPhoneOneTwoService;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @description: 手持机一二级修记录单列表
 * @date: 2021/10/12
 * @author: 冯帅
 */
@RestController
@RequestMapping("/mobileOneTwoFillBack")
public class PhoneOneTwoController {

    //日志类
    protected static final Logger logger = LoggerFactory.getLogger(PhoneOneTwoController.class);

    //接口出参状态码
    
    //加解密秘钥
    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    @Autowired
    IPhoneOneTwoService phoneOneTwoService;


    @ApiOperation(value = "手持机一二级修记录单列表查询")
    @PostMapping(value = "/getPhoneCheckListSummaryList", produces = "text/plain")
    @BussinessLog(value = "手持机一二级修记录单列表查询", key = "/mobileOneTwoFillBack/getPhoneCheckListSummaryList", type = "04")
    public String getPhoneCheckListSummaryList(@RequestParam String RWMT) {
        Date beginTime = new Date();
        logger.info("/mobileOneTwoFillBack/getPhoneCheckListSummaryList----开始调用手持机一二级修记录单列表查询接口...");
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject jsonObject = JSONObject.parseObject(key);
            QueryCheckListSummary queryModel = jsonObject.toJavaObject(QueryCheckListSummary.class);
            PhoneOneTwoRepairCheck res = phoneOneTwoService.getPhoneCheckList(queryModel);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", res);
            String msg = "查询成功";
            if(CollectionUtils.isEmpty(res.getOneTwoRepairCheckList())){
                msg = "当前没有配置记录单";
            }
            result.put("msg", msg);
        }catch (Exception e){
            logger.error("/mobileOneTwoFillBack/getPhoneCheckListSummaryList----调用手持机一二级修记录单列表查询接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "调用手持机一二级修记录单列表接口查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        Date endTime = new Date();
        logger.info("/mobileOneTwoFillBack/getPhoneCheckListSummaryList----调用查手持机一二级修记录单列表查询接口结束，共耗时："+(endTime.getTime() - beginTime.getTime()));
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    @ApiOperation(value = "手持机一二级修记录单单据详细信息查询")
    @PostMapping(value = "/getPhoneCheckDetailInfo", produces = "text/plain")
    @BussinessLog(value = "手持机一二级修记录单单据详细信息查询", key = "/mobileChecklist/getPhoneCheckDetailInfo", type = "04")
    public String getPhoneCheckDetailInfo(@RequestParam String RWMT){
        logger.info("/mobileOneTwoFillBack/getPhoneCheckDetailInfo----开始调用手持机一二级修记录单单据详细信息查询接口...");
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            OneTwoRepairCheckList queryModel = JSONObject.parseObject(key,OneTwoRepairCheckList.class);
            PhoneChecklistSummaryInfo phoneCheckDetailInfo = phoneOneTwoService.getPhoneCheckDetailInfo(queryModel);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", phoneCheckDetailInfo);
            result.put("msg", "查询成功");
        }catch (Exception e){
            logger.error("/mobileOneTwoFillBack/getPhoneCheckDetailInfo----调用手持机一二级修记录单单据详细信息查询接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "调用手持机一二级修记录单单据详细信息查询接口查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        logger.info("/mobileOneTwoFillBack/getPhoneCheckDetailInfo----调用手持机一二级修记录单单据详细信息查询接口结束...");
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    @ApiOperation(value = "手持机一二级修单据保存")
    @PostMapping(value = "/savePhoneRepairCell", produces = "text/plain")
    @BussinessLog(value = "手持机一二级修单据保存", key = "/mobileChecklist/savePhoneRepairCell", type = "04")
    public String savePhoneRepairCell(@RequestParam String RWMT){
        logger.info("/mobileOneTwoFillBack/savePhoneRepairCell----开始调用手持机一二级修单据保存接口...");
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            PhoneChecklistSummaryInfoForSave saveModel = JSONObject.parseObject(key, PhoneChecklistSummaryInfoForSave.class);
            ChecklistTriggerUrlCallResult res = phoneOneTwoService.savePhoneRepairCell(saveModel);
            if (!ObjectUtils.isEmpty(res)) {
                result.put("data",res);
                if(ObjectUtils.isEmpty(res.getAllowChange())||res.getAllowChange()){
                    result.put("code", RestResponseCodeEnum.SUCCESS);
                    result.put("msg","保存成功");
                }else{
                    result.put("code", RestResponseCodeEnum.NORMAL_FAIL);
                    result.put("msg",res.getOperationResultMessage());
                }
            }
        }catch (Exception e){
            logger.error("/mobileOneTwoFillBack/savePhoneRepairCell----调用手持机一二级修单据保存接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "调用手持机一二级修单据保存接口查询出错...");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        logger.info("/mobileOneTwoFillBack/savePhoneRepairCell----调用手持机一二级修单据保存接口结束...");
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    @ApiOperation(value = "手持机一二级修单据工长签字")
    @PostMapping(value = "/setPhoneOneTwoRepairForemanSign", produces = "text/plain")
    @BussinessLog(value = "手持机一二级修单据工长签字", key = "/mobileChecklist/setPhoneOneTwoRepairForemanSign", type = "04")
    public String setPhoneOneTwoRepairForemanSign(@RequestParam String RWMT){
        logger.info("/mobileOneTwoFillBack/setPhoneOneTwoRepairForemanSign----开始调用手持机一二级修单据工长签字接口...");
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            PhoneChecklistSummaryInfoForSave saveModel = JSONObject.parseObject(key,PhoneChecklistSummaryInfoForSave.class);
            ChecklistTriggerUrlCallResult res = phoneOneTwoService.setPhoneOneTwoRepairForemanSign(saveModel);
            if (!ObjectUtils.isEmpty(res)) {
                result.put("data",res);
                if(ObjectUtils.isEmpty(res.getAllowChange())||res.getAllowChange()){
                    result.put("code", RestResponseCodeEnum.SUCCESS);
                    result.put("msg",res.getOperationResultMessage());
                }else{
                    result.put("code", RestResponseCodeEnum.NORMAL_FAIL);
                    result.put("msg",res.getOperationResultMessage());
                }
            }
        }catch (Exception e){
            logger.error("/mobileOneTwoFillBack/setPhoneOneTwoRepairForemanSign----调用手持机一二级修单据工长签字接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "调用手持机一二级修单据工长签字接口查询出错...");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        logger.info("/mobileOneTwoFillBack/setPhoneOneTwoRepairForemanSign----调用手持机一二级修单据工长签字接口结束...");
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    @ApiOperation(value = "手持机一二级修单据质检员签字")
    @PostMapping(value = "/setPhoneOneTwoRepairQualitySign", produces = "text/plain")
    @BussinessLog(value = "手持机一二级修单据质检员签字", key = "/mobileChecklist/setPhoneOneTwoRepairQualitySign", type = "04")
    public String setPhoneOneTwoRepairQualitySign(@RequestParam String RWMT){
        logger.info("/mobileOneTwoFillBack/setPhoneOneTwoRepairQualitySign----开始调用手持机一二级修单据质检员签字接口...");
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            PhoneChecklistSummaryInfoForSave saveModel = JSONObject.parseObject(key,PhoneChecklistSummaryInfoForSave.class);
            ChecklistTriggerUrlCallResult res = phoneOneTwoService.setPhoneOneTwoRepairQualitySign(saveModel);
            if (!ObjectUtils.isEmpty(res)) {
                result.put("data",res);
                if(ObjectUtils.isEmpty(res.getAllowChange())||res.getAllowChange()){
                    result.put("code", RestResponseCodeEnum.SUCCESS);
                    result.put("msg",res.getOperationResultMessage());
                }else{
                    result.put("code", RestResponseCodeEnum.NORMAL_FAIL);
                    result.put("msg",res.getOperationResultMessage());
                }
            }
        }catch (Exception e){
            logger.error("/mobileOneTwoFillBack/setPhoneOneTwoRepairQualitySign----调用手持机一二级修单据质检员签字接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "调用手持机一二级修单据质检员签字接口查询出错...");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        logger.info("/mobileOneTwoFillBack/setPhoneOneTwoRepairQualitySign----调用手持机一二级修单据质检员签字接口结束...");
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    @ApiOperation(value = "手持机一二级修单据检修工人签字")
    @PostMapping(value = "/setPhoneOneTwoRepairPersonSign", produces = "text/plain")
    @BussinessLog(value = "手持机一二级修单据检修工人签字", key = "/mobileChecklist/setPhoneOneTwoRepairPersonSign", type = "04")
    public String setPhoneOneTwoRepairPersonSign(@RequestParam String RWMT){
        logger.info("/mobileOneTwoFillBack/setPhoneOneTwoRepairPersonSign----开始调用手持机一二级修单据检修工人签字接口...");
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            PhoneChecklistSummaryInfoForSave saveModel = JSONObject.parseObject(key,PhoneChecklistSummaryInfoForSave.class);
            ChecklistTriggerUrlCallResult res = phoneOneTwoService.setPhoneOneTwoRepairPersonSign(saveModel);
            if (!ObjectUtils.isEmpty(res)) {
                result.put("data",res);
                if(ObjectUtils.isEmpty(res.getAllowChange())||res.getAllowChange()){
                    result.put("code", RestResponseCodeEnum.SUCCESS);
                    result.put("msg",res.getOperationResultMessage());
                }else{
                    result.put("code", RestResponseCodeEnum.NORMAL_FAIL);
                    result.put("msg",res.getOperationResultMessage());
                }
            }
        }catch (Exception e){
            logger.error("/mobileOneTwoFillBack/setPhoneOneTwoRepairPersonSign----调用手持机一二级修单据检修工人签字接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "调用手持机一二级修单据检修工人签字接口查询出错...");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        logger.info("/mobileOneTwoFillBack/setPhoneOneTwoRepairPersonSign----调用手持机一二级修单据检修工人签字接口结束...");
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }


    @PostMapping(value = "/test")
    @ResponseBody
    public JSONObject test(@RequestBody QueryCheckListSummary queryModel) {
        JSONObject result = new JSONObject();
        try{
            //获取列表--测试
            PhoneOneTwoRepairCheck res = phoneOneTwoService.getPhoneCheckList(queryModel);
            result.put("msg","成功");
            result.put("code",RestResponseCodeEnum.SUCCESS);
            result.put("data",res);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, logger, "手持机作业过程一级修车组查询失败");
        }
        return result;
    }

    @PostMapping(value = "/test2")
    @ResponseBody
    public RestResult test2(@RequestBody OneTwoRepairCheckList queryModel) {
        RestResult result = RestResult.success();
        try{
            //获取详细数据--测试
            PhoneChecklistSummaryInfo res = phoneOneTwoService.getPhoneCheckDetailInfo(queryModel);
            result.setMsg("成功");
            result.setData(res);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, logger, "手持机作业过程一级修车组查询失败");
        }
        return result;
    }
}
