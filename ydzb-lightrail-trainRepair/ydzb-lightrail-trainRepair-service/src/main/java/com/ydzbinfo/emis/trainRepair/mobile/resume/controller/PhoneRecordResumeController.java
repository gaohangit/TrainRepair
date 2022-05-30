package com.ydzbinfo.emis.trainRepair.mobile.resume.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.mobile.resume.service.IPhoneRecordResumeService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 履历控制器
 * Author: wuyuechang
 * Create Date Time: 2021/4/29 10:51
 * Update Date Time: 2021/4/29 10:51
 *
 * @see
 */
@RestController
@RequestMapping("/mobileResume")
@Slf4j
public class PhoneRecordResumeController {

    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    
    @Autowired
    private IPhoneRecordResumeService phoneRecordResumeService;

    /**
     * @author: wuyuechang
     *
     * @Description: 根据单位编码获取接送车到过本单位的车组列表
     * @param: departmentcode    单位编码
     * @return 车组列表
     * @date: 2021/4/29 10:51
     */
    @GetMapping(value = "/getTrainsetListReceived", produces = "text/plain")
    @ApiOperation("根据单位编码获取接送车到过本单位的车组列表")
    @BussinessLog(value = "根据单位编码获取接送车到过本单位的车组列表", key = "/mobileResume/getTrainsetListReceived", type = "04")
    public String getTrainsetListReceived(@RequestParam String RWMT){
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            List<TrainsetBaseInfo> trainsetListReceived = phoneRecordResumeService.getTrainsetListReceived(unitCode);
            result.put("msg", "成功");
            result.put("data", trainsetListReceived);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "根据单位编码获取接送车到过本单位的车组列表失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @author: wuyuechang
     *
     * @Description: 根据车组号获取车组的车号信息
     * @param: trainsetid    车组id
     * @return 车号信息
     * @date: 2021/4/29 10:51
     */
    @GetMapping(value = "/getCarno", produces = "text/plain")
    @ApiOperation("根据车组号获取车组的车号信息")
    @BussinessLog(value = "根据车组号获取车组的车号信息", key = "/mobileResume/getCarno", type = "04")
    public String getCarno(@RequestParam String RWMT){
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String trainsetId = param.getString("trainsetId");
            List<String> carnos = phoneRecordResumeService.getCarno(trainsetId);
            result.put("msg", "成功");
            result.put("data", carnos);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "根据车组号获取车组的车号信息失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @author: wuyuechang
     *
     * @Description: 根据车组id和日期查询走行公里
     * @param: trainsetid    车组id
     * @param: querydate    日期
     * @return 车号信息
     * @date: 2021/4/29 10:51
     */
    @PostMapping(value = "/getTrainsetAccMile", produces = "text/plain")
    @ApiOperation("根据车组id和日期查询走行公里")
    @BussinessLog(value = "根据车组id和日期查询走行公里", key = "/mobileResume/getTrainsetAccMile", type = "04")
    public String getTrainsetAccMile(@RequestParam String RWMT){
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String trainsetId = param.getString("trainsetId");
            String querydate = param.getString("querydate");
            int data = phoneRecordResumeService.getTrainsetAccMile(trainsetId, querydate);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "根据车组id和日期查询走行公里失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @author: wuyuechang
     *
     * @Description: 查询装车部件履历
     * @param: trainsetid    车组id
     * @param: querydate    日期
     * @return 车号信息
     * @date: 2021/4/29 10:51
     */
    @PostMapping(value = "/getPartListOnTrainset", produces = "text/plain")
    @ApiOperation("查询装车部件履历")
    @BussinessLog(value = "查询装车部件履历", key = "/mobileResume/getPartListOnTrainset", type = "04")
    public String getPartListOnTrainset(@RequestParam String RWMT){
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String trainsetId = param.getString("trainsetId");
            JSONArray data = phoneRecordResumeService.getPartListOnTrainset(trainsetId);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "查询装车部件履历失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @author: wuyuechang
     *
     * @Description: 查询构型节点
     * @param: trainsetid    车组id
     * @param: sCarNo    辆序
     * @return 批次构型辆序的节点集合
     * @date: 2021/4/30 10:20
     */
    @GetMapping(value = "/getBatchBomNodeListByCarNo", produces = "text/plain")
    @ApiOperation("查询构型节点")
    @BussinessLog(value = "查询构型节点", key = "/mobileResume/getBatchBomNodeListByCarNo", type = "04")
    public String getBatchBomNodeListByCarNo(@RequestParam String RWMT){
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String trainsetId = param.getString("trainsetId");
            String sCarNo = param.getString("sCarNo");
            JSONArray data = phoneRecordResumeService.getBatchBomNodeListByCarNo(trainsetId, sCarNo);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "查询构型节点失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @author: wuyuechang
     *
     * @Description: 获取配件列表
     * @param: trainsetid    车组id
     * @param: sCarNo    辆序
     * @return 配件列表集合
     * @date: 2021/4/30 10:50
     */
    @GetMapping(value = "/partsTypeByName", produces = "text/plain")
    @ApiOperation("获取配件列表")
    @BussinessLog(value = "获取配件列表", key = "/mobileResume/partsTypeByName", type = "04")
    public String partsTypeByName(@RequestParam(required = false) String RWMT){
        JSONObject result = new JSONObject();
        try{
            String partsTypeName = null;
            if (StringUtils.isNotBlank(RWMT)){
                String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
                JSONObject param = JSONObject.parseObject(key);
                partsTypeName = param.getString("partsTypeName");
            }
            JSONArray data = phoneRecordResumeService.partsTypeByName(partsTypeName);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "获取配件列表失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }
}
