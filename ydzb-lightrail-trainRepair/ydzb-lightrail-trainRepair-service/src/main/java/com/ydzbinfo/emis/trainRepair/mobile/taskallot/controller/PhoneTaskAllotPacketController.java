package com.ydzbinfo.emis.trainRepair.mobile.taskallot.controller;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.mobile.model.*;
import com.ydzbinfo.emis.trainRepair.mobile.taskallot.service.IPhoneTaskAllotPacketService;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskAllotPhoneTrainSetState;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Description: 手持机派工控制器
 * <p>
 * Author: wuyuechang
 * Create Date Time: 2021/4/23 11:26
 * Update Date Time: 2021/4/23 11:26
 *
 * @see com.ydzbinfo.emis.trainRepair.taskAllot.controller.TaskAllotPacketEndpoint ;
 */
@RestController
@RequestMapping("/mobileAllotTask")
@Slf4j
public class PhoneTaskAllotPacketController {

    //日志类
    protected static final Logger logger = LoggerFactory.getLogger(PhoneTaskAllotPacketController.class);

    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    @Autowired
    private IPhoneTaskAllotPacketService iPhoneTaskAllotPacketService;


    /**
     * @return 手持机检修任务
     * @author: wuyuechang
     * @Description: 手持机获取检修任务
     * @param: unitCode    所编码
     * @param: unitName    所名称
     * @param: dayplanId    班次
     * @param: deptCode    部门Code
     * @param: deptName    部门名称
     * @date: 2021/4/23 15:15
     */
    @ApiOperation("手持机获取检修任务")
    @RequestMapping(value = "/getMobileAllotTask", produces = "text/plain")
    @BussinessLog(value = "手持机获取检修任务", key = "/mobileAllotTask/getMobileAllotTask", type = "04")
    public String getPhoneTaskAllot(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取检修任务入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String dayPlanID = param.getString("dayPlanID");
            String deptCode = param.getString("deptCode");
            result = iPhoneTaskAllotPacketService.getPhoneTaskAllot(unitCode, dayPlanID, deptCode);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机获取检修任务数据失败");
            logger.error("手持机获取检修任务数据失败", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation("手持机获取检修任务调试")
    @GetMapping(value = "/getPhoneTaskAllotTest")
    @BussinessLog(value = "手持机获取检修任务调试", key = "/mobileAllotTask/getPhoneTaskAllotTest", type = "04")
    public JSONObject getPhoneTaskAllotTest(@RequestParam("unitCode") String unitCode, @RequestParam("dayPlanID") String dayPlanID, @RequestParam("deptCode") String deptCode) {
        JSONObject result = new JSONObject();
        try {
            result = iPhoneTaskAllotPacketService.getPhoneTaskAllot(unitCode, dayPlanID, deptCode);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机获取检修任务调试");
            logger.error("手持机获取检修任务调试", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return result;
    }

    /***
     * @author: 冯帅
     * @date: 2021/9/3
     * @param: [RWMT]
     * @return: java.lang.String
     */
    @ApiOperation("手持机获取车组派工状态")
    @RequestMapping(value = "/getPhoneTaskTrainSetState", produces = "text/plain")
    @BussinessLog(value = "手持机获取车组派工状态", key = "/mobileAllotTask/getPhoneTaskTrainSetState", type = "04")
    public String getPhoneTaskTrainSetState(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String dayPlanId = param.getString("dayPlanId");
            String deptCode = param.getString("deptCode");
            // String unitCode = "018";
            // String dayPlanId = "2021-09-03-00";
            // String deptCode = "";
            List<TaskAllotPhoneTrainSetState> taskAllotPhoneTrainSetStateList = iPhoneTaskAllotPacketService.getPhoneTaskTrainSetState(unitCode, deptCode, dayPlanId);
            logger.info("--------车组派工状态：" + taskAllotPhoneTrainSetStateList);
            result.put("data", taskAllotPhoneTrainSetStateList);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception ex) {
            RestResult restResult = RestResult.fromException(ex, log, "手持机获取车组派工状态");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @return 班组信息
     * @author: wuyuechang
     * @Description: 手持机获取班组
     * @param: deptCode    部门Code
     * @date: 2021/4/23 17:15
     */
    @ApiOperation("手持机获取班组信息")
    @RequestMapping(value = "/getMobileGroup", produces = "text/plain")
    @BussinessLog(value = "手持机获取班组信息", key = "/mobileAllotTask/getMobileGroup   ", type = "04")
    public String getMobileGroup(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取班组信息入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String deptCode = param.getString("deptCode");
            Map mobileGroup = iPhoneTaskAllotPacketService.getMobileGroup(deptCode);
            log.trace("手持机获取班组信息出参:{}", mobileGroup);
            result.put("data", mobileGroup.get("data"));
            result.put("deptUserList", mobileGroup.get("deptUserList"));
            result.put("msg", mobileGroup.get("msg"));
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "手持机获取班组数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation("手持机增加派工数据")
    @RequestMapping(value = "/setMobileAllotTask", produces = "text/plain")
    @BussinessLog(value = "手持机增加派工数据", key = "/mobileAllotTask/setMobileAllotTask   ", type = "04")
    public String setMobileAllotTask(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            iPhoneTaskAllotPacketService.setMobileAllotTask(param);
            result.put("msg", "操作成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "手持机新增派工数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * 以下代码暂时用不到，为派工优化代码
     *
     * @return
     */
    @ApiOperation("手持机获取计划车组信息")
    @RequestMapping(value = "/getPlanTrainset", produces = "text/plain")
    @BussinessLog(value = "手持机获取计划车组信息", key = "/mobileAllotTask/getPlanTrainset", type = "04")
    public String getPlanTrainset(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            //这里需要考虑故障包与复核包的情况，判断派工状态。
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取计划车组信息入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String dayPlanId = param.getString("dayPlanId");
            String deptCode = param.getString("deptCode");
            List<PlanTrainset> planTrainsetList = iPhoneTaskAllotPacketService.getPlanTrainset(dayPlanId, unitCode, deptCode);
            result.put("data", planTrainsetList);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机获取计划车组信息失败");
            logger.error("手持机获取计划车组信息失败", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation("手持机派工优化测试数据接口")
    @RequestMapping(value = "/getTestData", produces = "text/plain")
    @BussinessLog(value = "手持机获取计划车组信息", key = "/mobileAllotTask/getTestData", type = "04")
    public JSONObject getTestData(@RequestBody JSONObject testParams) {
        JSONObject result = new JSONObject();
        try{
            String unitCode = testParams.getString("unitCode");
            String dayPlanId = testParams.getString("dayPlanId");
            String deptCode = testParams.getString("deptCode");
            List<PlanTrainset> planTrainsetList = iPhoneTaskAllotPacketService.getPlanTrainset(dayPlanId, unitCode, deptCode);
            result.put("data", planTrainsetList);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        }catch (Exception ex){
            logger.error("失败...",ex);
        }
        return result;
    }

    /**
     * 以下代码暂时用不到，为派工优化代码
     *
     * @return
     */
    @ApiOperation("手持机获取计划包信息")
    @RequestMapping(value = "/getPlanPacket", produces = "text/plain")
    @BussinessLog(value = "手持机获取计划包信息", key = "/mobileAllotTask/getPlanPacket", type = "04")
    public String getPlanPacket(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取计划包信息入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String dayPlanId = param.getString("dayPlanId");
            String deptCode = param.getString("deptCode");
            String trainsetId = param.getString("trainsetId");
//             String unitCode = "018";
//             String dayPlanId = "2022-04-15-00";
//             String deptCode = "0057600036";
//             String trainsetId = "CR400AF-2124";
            List<PlanPacket> planPacketList = iPhoneTaskAllotPacketService.getPlanPacket(dayPlanId, unitCode, deptCode, trainsetId);
            result.put("data", planPacketList);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机获取计划包信息失败");
            logger.error("手持机获取计划包信息失败", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * 以下代码暂时用不到，为派工优化代码
     *
     * @return
     */
    @ApiOperation("手持机获取计划项目信息")
    @RequestMapping(value = "/getPlanItem", produces = "text/plain")
    @BussinessLog(value = "手持机获取计划项目信息", key = "/mobileAllotTask/getPlanItem", type = "04")
    public String getPlanItem(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取计划项目信息入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String dayPlanId = param.getString("dayPlanId");
            String deptCode = param.getString("deptCode");
            String trainsetId = param.getString("trainsetId");
            String packetCode = param.getString("packetCode");
//             String unitCode = "018";
//             String dayPlanId = "2022-04-15-00";
//             String deptCode = "0057600036";
//             String trainsetId = "CR400AF-2124";
//             String packetCode = "yb000000000000000001"; //故障处理任务包
            //剩余是否新项目没有写
            List<PlanItem> planItemList = iPhoneTaskAllotPacketService.getPlanItem(dayPlanId, unitCode, deptCode, trainsetId, packetCode);
            result.put("data", planItemList);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机获取计划项目信息失败");
            logger.error("手持机获取计划项目信息失败", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * 以下代码暂时用不到，为派工优化代码
     *
     * @return
     */
    @ApiOperation("手持机获取辆序或部件信息")
    @RequestMapping(value = "/getPlanCarNoOrPart", produces = "text/plain")
    @BussinessLog(value = "手持机获取辆序或部件信息", key = "/mobileAllotTask/getPlanCarNoOrPart", type = "04")
    public String getPlanCarNoOrPart(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取辆序或部件信息入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String dayPlanId = param.getString("dayPlanId");
            String deptCode = param.getString("deptCode");
            String trainsetId = param.getString("trainsetId");
            String packetCode = param.getString("packetCode");
            String itemCode = param.getString("itemCode");
            String displayItemName = param.getString("displayItemName");
//             String unitCode = "018";
//             String dayPlanId = "2022-04-15-00";
//             String deptCode = "0057600036";
//             String trainsetId = "CR400AF-2124";
//             String packetCode = "yb000000000000000001";//故障处理包
//             String itemCode = "018202204150903058305809";
//             String displayItemName = "";
            List<PlanCarNoOrPart> planItemList = iPhoneTaskAllotPacketService.getPlanCarNoOrPart(dayPlanId, unitCode, deptCode, trainsetId, packetCode, itemCode, displayItemName);
            result.put("data", planItemList);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机获取辆序或部件信息失败");
            logger.error("手持机获取辆序或部件信息失败", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * 以下代码暂时用不到，为派工优化代码
     *
     * @return
     */
    @ApiOperation("手持机获取计划派工人员信息")
    @RequestMapping(value = "/getPlanAllot", produces = "text/plain")
    @BussinessLog(value = "手持机获取计划派工人员信息", key = "/mobileAllotTask/getPlanAllot", type = "04")
    public String getPlanAllot(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            log.info("手持机获取计划派工人员信息入参:{}", key);
            JSONObject param = JSONObject.parseObject(key);
            String unitCode = param.getString("unitCode");
            String dayPlanId = param.getString("dayPlanId");
            String deptCode = param.getString("deptCode");
            String trainsetId = param.getString("trainsetId");
            String packetCode = param.getString("packetCode");
            String itemCode = param.getString("itemCode");
            String displayItemName = param.getString("displayItemName");
//             String unitCode = "018";
//             String dayPlanId = "2022-04-15-00";
//             String deptCode = "0057600036";
//             String trainsetId = "CR400AF-2124";
//             String packetCode = "yb000000000000000001";
//             String itemCode = "018202204150903058305809";
//             String displayItemName = "";
            List<PlanAllotInfo> planAllotInfo = iPhoneTaskAllotPacketService.getPlanAllotInfo(dayPlanId, unitCode, deptCode, trainsetId, packetCode, itemCode, displayItemName);
            result.put("data", planAllotInfo);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机获取计划派工人员信息失败");
            logger.error("手持机获取计划派工人员信息失败", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * 以下代码暂时用不到，为派工优化代码
     *
     * @return
     */
    @ApiOperation("手持机提交派工信息")
    @PostMapping(value = "/submitAllotInfo", produces = "text/plain")
    @BussinessLog(value = "手持机提交派工信息", key = "/mobileAllotTask/submitAllotInfo", type = "04")
    public JSONObject submitAllotInfo(@RequestBody AllotInfo allotInfo) {
        JSONObject result = new JSONObject();
        try {
            String unitCode = allotInfo.getUnitCode();
            String unitName = allotInfo.getUnitName();
            String deptCode = allotInfo.getDeptCode();
            String deptName = allotInfo.getDeptName();
            String dayPlanId = allotInfo.getDayPlanId();
            List<PlanWorker> planWorkerList = allotInfo.getPlanWorkerList();
            List<AllotTrainset> allotTrainsetList = allotInfo.getAllotTrainsetList();
            List<TaskAllotPacketEntity> taskAllotPacketEntities = iPhoneTaskAllotPacketService.submitAllotInfo(unitCode, unitName, deptCode, deptName, dayPlanId, planWorkerList, allotTrainsetList);
            result.put("data", taskAllotPacketEntities);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, logger, "手持机提交派工信息失败");
            logger.error("手持机提交派工信息失败", e);
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return result;
    }

}