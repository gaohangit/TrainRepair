package com.ydzbinfo.emis.trainRepair.mobile.taskpandect.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.mobile.model.MobileTaskPandect;
import com.ydzbinfo.emis.trainRepair.mobile.taskpandect.service.IPhoneTaskPandectService;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 任务总单控制器
 * Author: wuyuechang
 * Create Date Time: 2021/4/26 14:09
 * Update Date Time: 2021/4/26 14:09
 *
 * @see
 */
@RestController
@RequestMapping("/mobilePandect")
@Slf4j
public class PhoneTaskPandectController {

    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    @Autowired
    private IPhoneTaskPandectService phoneTaskPandectService;

    
    /**
     * @author: wuyuechang
     *
     * @Description: 手持机获取检修任务
     * @param: unitCode    所编码
     * @param: dayPlanID	日计划id
     * @param: workerId	人员编码
     * @param: deptCode	班组code
     * @return 手持机任务总单
     * @date: 2021/4/23 15:15
     */
    @GetMapping(value = "/getMobileTaskPandect", produces = "text/plain")
    @ApiOperation("手持机获取任务总单")
    @BussinessLog(value = "手持机获取任务总单", key = "/mobilePandect/getMobileTaskPandect", type = "04")
    public String getPhoneTaskPandect(@RequestParam String RWMT){
        long startNow = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取任务总单入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String workerId = param.getString("workerId");
            JSONArray deptCodes = param.getJSONArray("deptCodeList");
            List<String> deptCodeList = new ArrayList<>();
            if(!ObjectUtils.isEmpty(deptCodes)){
                deptCodeList = deptCodes.toJavaList(String.class);
            }
            String dayPlanID = param.getString("dayPlanID");
            String isSelf = param.getString("isSelf");
            List<MobileTaskPandect> data = phoneTaskPandectService.getPhoneTaskPandect(unitCode, workerId, dayPlanID, deptCodeList, isSelf);
            log.info("手持机获取任务总单出参:{}", data);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机获取任务总单数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        long endNow = System.currentTimeMillis();
        log.info("手持机获取任务总单执行时间:{}", endNow - startNow);
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @author: wuyuechang
     *
     * @Description: 手持机获取检修计划
     * @param: unitCode    所编码
     * @param: dayPlanID	日计划id
     * @param: deptCode	班组code
     * @return 手持机任务总单
     * @date: 2021/4/23 15:15
     */
    @GetMapping(value = "/getMobileRepairPlan", produces = "text/plain")
    @ApiOperation("手持机获取检修计划")
    @BussinessLog(value = "手持机获取检修计划", key = "/mobilePandect/getMobileRepairPlan", type = "04")
    public String getMobileRepairPlan(@RequestParam String RWMT){
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取检修计划入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String deptCode = param.getString("deptCode");
            List<String> deptCodeList = param.getJSONArray("deptCodeList").toJavaList(String.class);
            String dayPlanID = param.getString("dayPlanID");
            List<MobileTaskPandect> data = phoneTaskPandectService.getMobileRepairPlan(unitCode, dayPlanID, deptCode);
            log.info("手持机获取检修计划出参:{}", data);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, log, "手持机获取检修计划数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }
}
