package com.ydzbinfo.emis.common.taskAllot.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.common.taskAllot.service.XzyMTaskAllotService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: fengshuai
 * @Date: 2021/3/31
 * @Description: 派工作业过程数据处理（中台服务）
 */
@RestController
@RequestMapping("/xzyMTaskallot")
public class XzyMTaskAllotController {
    //日志对象
    protected static final Logger logger = LoggerFactory.getLogger(XzyMTaskAllotController.class);

    @Autowired
    XzyMTaskAllotService xzyMTaskAllotService;


    @ApiOperation("中台获取派工数据（到岗位）-根据日计划")
    @PostMapping("/getTaskAllotData")
    @ResponseBody
    @BussinessLog(value = "中台获取派工数据（到岗位）-根据日计划", key = "/xzyMTaskallot/getTaskAllotData")
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult getTaskAllotData(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyMTaskallot/getTaskAllotData----开始调用中台获取派工数据（到岗位）-根据日计划接口...");
        RestResult result = RestResult.success();
        try {
            //1.接收参数
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String repairType = jsonObject.getString("repairType");
            JSONArray trainsetTypes = jsonObject.getJSONArray("trainsetTypeList");
            JSONArray trainsetIds = jsonObject.getJSONArray("trainsetIdList");
            JSONArray itemCodes = jsonObject.getJSONArray("itemCodeList");
            JSONArray branchCodes = jsonObject.getJSONArray("branchCodeList");
            JSONArray workIds = jsonObject.getJSONArray("workIdList");
            String packetName = jsonObject.getString("packetName");
            //2.转换参数类型
            List<String> trainsetTypeList = null;
            List<String> trainsetIdList = null;
            List<String> itemCodeList = null;
            List<String> branchCodeList = null;
            List<String> workIdList = null;
            if (trainsetTypes != null && trainsetTypes.size() > 0) {
                trainsetTypeList = trainsetTypes.toJavaList(String.class);
            }
            if (trainsetIds != null && trainsetIds.size() > 0) {
                trainsetIdList = trainsetIds.toJavaList(String.class);
            }
            if (itemCodes != null && itemCodes.size() > 0) {
                itemCodeList = itemCodes.toJavaList(String.class);
            }
            if (branchCodes != null && branchCodes.size() > 0) {
                branchCodeList = branchCodes.toJavaList(String.class);
            }
            if (workIds != null && workIds.size() > 0) {
                workIdList = workIds.toJavaList(String.class);
            }
            //如果有车组ID条件，就把车型的条件忽略掉
            if (trainsetIdList != null && trainsetIdList.size() > 0) {
                trainsetTypeList = null;
            }
            //3.从数据库中取数据
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = xzyMTaskAllotService.getTaskAllotData(unitCode, dayPlanId, null, null, repairType, trainsetTypeList, trainsetIdList, itemCodeList, branchCodeList, workIdList, packetName);
            //4.设置返回结果
            result.setData(taskAllotPacketEntityList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/getTaskAllotData----调用中台获取派工数据（到岗位）-根据日计划接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyMTaskallot/getTaskAllotData----调用中台获取派工数据（到岗位）-根据日计划接口结束...");
        return result;
    }

    @ApiOperation("中台获取派工数据（到人员）-根据日计划")
    @PostMapping("/getTaskAllotDataToPerson")
    @ResponseBody
    @BussinessLog(value = "中台获取派工数据（到人员）-根据日计划", key = "/xzyMTaskallot/getTaskAllotData")
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult getTaskAllotDataToPerson(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyMTaskallot/getTaskAllotDataToPerson----开始调用中台获取派工数据（到人员）-根据日计划接口...");
        RestResult result = RestResult.success();
        try {
            //1.接收参数
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String repairType = jsonObject.getString("repairType");
            JSONArray trainsetTypes = jsonObject.getJSONArray("trainsetTypeList");
            JSONArray trainsetIds = jsonObject.getJSONArray("trainsetIdList");
            JSONArray itemCodes = jsonObject.getJSONArray("itemCodeList");
            JSONArray branchCodes = jsonObject.getJSONArray("branchCodeList");
            JSONArray workIds = jsonObject.getJSONArray("workIdList");
            String packetName = jsonObject.getString("packetName");
            //2.转换参数类型
            List<String> trainsetTypeList = null;
            List<String> trainsetIdList = null;
            List<String> itemCodeList = null;
            List<String> branchCodeList = null;
            List<String> workIdList = null;
            if (trainsetTypes != null && trainsetTypes.size() > 0) {
                trainsetTypeList = trainsetTypes.toJavaList(String.class);
            }
            if (trainsetIds != null && trainsetIds.size() > 0) {
                trainsetIdList = trainsetIds.toJavaList(String.class);
            }
            if (itemCodes != null && itemCodes.size() > 0) {
                itemCodeList = itemCodes.toJavaList(String.class);
            }
            if (branchCodes != null && branchCodes.size() > 0) {
                branchCodeList = branchCodes.toJavaList(String.class);
            }
            if (workIds != null && workIds.size() > 0) {
                workIdList = workIds.toJavaList(String.class);
            }
            //如果有车组ID条件，就把车型的条件忽略掉
            if (trainsetIdList != null && trainsetIdList.size() > 0) {
                trainsetTypeList = null;
            }
            //3.从数据库中取数据
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = xzyMTaskAllotService.getTaskAllotDataToPerson(unitCode, dayPlanId, null, null, repairType, trainsetTypeList, trainsetIdList, itemCodeList, branchCodeList, workIdList, packetName);
            //4.设置返回结果
            result.setData(taskAllotPacketEntityList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/getTaskAllotDataToPerson----调用中台获取派工数据（到人员）-根据日计划接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyMTaskallot/getTaskAllotDataToPerson----调用中台获取派工数据（到人员）-根据日计划接口结束...");
        return result;
    }

    @ApiOperation("中台获取派工数据（到项目）—根据日计划")
    @PostMapping("/getTaskAllotDataToItem")
    @ResponseBody
    @BussinessLog(value = "中台获取派工数据（到项目）—根据日计划", key = "/xzyMTaskallot/getTaskAllotData")
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult getTaskAllotDataToItem(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyMTaskallot/getTaskAllotDataToItem----开始调用中台获取派工数据（到项目）—根据日计划接口...");
        RestResult result = RestResult.success();
        try {
            //1.接收参数
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String repairType = jsonObject.getString("repairType");
            JSONArray trainsetTypes = jsonObject.getJSONArray("trainsetTypeList");
            JSONArray trainsetIds = jsonObject.getJSONArray("trainsetIdList");
            JSONArray itemCodes = jsonObject.getJSONArray("itemCodeList");
            JSONArray branchCodes = jsonObject.getJSONArray("branchCodeList");
            String packetName = jsonObject.getString("packetName");
            //2.转换参数类型
            List<String> trainsetTypeList = null;
            List<String> trainsetIdList = null;
            List<String> itemCodeList = null;
            List<String> branchCodeList = null;
            if (trainsetTypes != null && trainsetTypes.size() > 0) {
                trainsetTypeList = trainsetTypes.toJavaList(String.class);
            }
            if (trainsetIds != null && trainsetIds.size() > 0) {
                trainsetIdList = trainsetIds.toJavaList(String.class);
            }
            if (itemCodes != null && itemCodes.size() > 0) {
                itemCodeList = itemCodes.toJavaList(String.class);
            }
            if (branchCodes != null && branchCodes.size() > 0) {
                branchCodeList = branchCodes.toJavaList(String.class);
            }
            //如果有车组ID条件，就把车型的条件忽略掉
            if (trainsetIdList != null && trainsetIdList.size() > 0) {
                trainsetTypeList = null;
            }
            //3.从数据库中取数据
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = xzyMTaskAllotService.getTaskAllotDataToItem(unitCode, dayPlanId,repairType, trainsetTypeList, trainsetIdList, itemCodeList, branchCodeList, packetName);
            //4.设置返回结果
            result.setData(taskAllotPacketEntityList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/getTaskAllotDataToItem----调用中台获取派工数据（到项目）—根据日计划接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyMTaskallot/getTaskAllotDataToItem----调用中台获取派工数据（到项目）—根据日计划接口结束...");
        return result;
    }

    @ApiOperation("中台获取派工车组集合-根据日计划")
    @PostMapping("/getTaskAllotTrainsetList")
    @ResponseBody
    @BussinessLog(value = "中台获取派工车组集合-根据日计划", key = "/xzyMTaskallot/getTaskAllotData")
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult getTaskAllotTrainsetList(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyMTaskallot/getTaskAllotTrainsetList----开始调用中台获取派工车组集合-根据日计划接口...");
        RestResult result = RestResult.success();
        try {
            //1.接收参数
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String mainCyc = jsonObject.getString("mainCyc");
            String branchCode = jsonObject.getString("branchCode");
            //3.从数据库中取数据
            List<String> trainsetIdList = xzyMTaskAllotService.getTaskAllotTrainsetList(unitCode, dayPlanId, branchCode, mainCyc);
            //4.设置返回结果
            result.setData(trainsetIdList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/getTaskAllotTrainsetList----调用中台中台获取派工车组集合数据-根据日计划接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyMTaskallot/getTaskAllotTrainsetList----调用中台中台获取派工车组集合数据-根据日计划接口结束...");
        return result;
    }

    @ApiOperation("中台获取机检一级修派工车组集合-根据日计划")
    @PostMapping("/getTaskAllotEquipmentTrainsetList")
    @ResponseBody
    @BussinessLog(value = "中台获取机检一级修派工车组集合-根据日计划", key = "/xzyMTaskallot/getTaskAllotData")
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult getTaskAllotEquipmentTrainsetList(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyMTaskallot/getTaskAllotEquipmentTrainsetList----中台获取机检一级修派工车组集合-根据日计划接口...");
        RestResult result = RestResult.success();
        try {
            //1.接收参数
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String branchCode = jsonObject.getString("branchCode");
            //3.从数据库中取数据
            List<String> trainsetIdList = xzyMTaskAllotService.getTaskAllotEquipmentTrainsetList(unitCode, dayPlanId, branchCode);
            //4.设置返回结果
            result.setData(trainsetIdList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/getTaskAllotEquipmentTrainsetList----中台获取机检一级修派工车组集合-根据日计划接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyMTaskallot/getTaskAllotTrainsetList----中台获取机检一级修派工车组集合-根据日计划接口结束...");
        return result;
    }

    @ApiOperation("中台获取派工数据-根据时间段")
    @PostMapping("/getTaskAllotDataByDate")
    @ResponseBody
    @BussinessLog(value = "中台获取派工数据-根据时间段", key = "/xzyMTaskallot/getTaskAllotDataByDate")
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult getTaskAllotDataByDate(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyMTaskallot/getTaskAllotDataByDate----开始调用中台获取派工数据-根据时间段接口...");
        RestResult result = RestResult.success();
        try {
            //1.接收参数
            String unitCode = jsonObject.getString("unitCode");
            String repairType = jsonObject.getString("repairType");
            String startDate = jsonObject.getString("startDate");
            String endDate = jsonObject.getString("endDate");
            JSONArray trainsetTypes = jsonObject.getJSONArray("trainsetTypeList");
            JSONArray trainsetIds = jsonObject.getJSONArray("trainsetIdList");
            JSONArray itemCodes = jsonObject.getJSONArray("itemCodeList");
            JSONArray branchCodes = jsonObject.getJSONArray("branchCodeList");
            JSONArray workIds = jsonObject.getJSONArray("workIdList");
            String packetName = jsonObject.getString("packetName");
            //2.转换参数类型
            List<String> trainsetTypeList = null;
            List<String> trainsetIdList = null;
            List<String> itemCodeList = null;
            List<String> branchCodeList = null;
            List<String> workIdList = null;
            if (trainsetTypes != null && trainsetTypes.size() > 0) {
                trainsetTypeList = trainsetTypes.toJavaList(String.class);
            }
            if (trainsetIds != null && trainsetIds.size() > 0) {
                trainsetIdList = trainsetIds.toJavaList(String.class);
            }
            if (itemCodes != null && itemCodes.size() > 0) {
                itemCodeList = itemCodes.toJavaList(String.class);
            }
            if (branchCodes != null && branchCodes.size() > 0) {
                branchCodeList = branchCodes.toJavaList(String.class);
            }
            if (workIds != null && workIds.size() > 0) {
                workIdList = workIds.toJavaList(String.class);
            }
            //如果有车组ID条件，就把车型的条件忽略掉
            if (trainsetIdList != null && trainsetIdList.size() > 0) {
                trainsetTypeList = null;
            }
            //3.从数据库中取数据
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = xzyMTaskAllotService.getTaskAllotData(unitCode, null, startDate, endDate, repairType, trainsetTypeList, trainsetIdList, itemCodeList, branchCodeList, workIdList, packetName);
            //4.设置返回结果
            result.setData(taskAllotPacketEntityList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/getTaskAllotDataByDate----调用中台获取派工数据-根据时间段接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyMTaskallot/getTaskAllotDataByDate----调用中台获取派工数据-根据时间段接口结束...");
        return result;
    }

    @ApiOperation("中台插入派工数据")
    @PostMapping("/setTaskAllotData")
    @ResponseBody
    @BussinessLog(value = "中台插入派工数据", key = "/xzyMTaskallot/setTaskAllotData")
    public RestResult setTaskAllotData(@RequestBody List<TaskAllotPacketEntity> taskAllotPacketList) {
        logger.info("/xzyMTaskallot/setTaskAllotData----开始调用中台插入派工数据接口...");
        RestResult result = RestResult.success();
        try {
            if (taskAllotPacketList == null || taskAllotPacketList.size() == 0) {
                throw new RuntimeException("失败：无数据，请检查参数!");
            } else {
                boolean flag = xzyMTaskAllotService.setTaskAllotData(taskAllotPacketList);
                result.setMsg("插入成功");
            }
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/setTaskAllotData----调用中台插入派工数据接口出错...", ex);
            result = RestResult.fromException(ex, logger, "插入失败");
        }
        logger.info("/xzyMTaskallot/setTaskAllotData----调用中台插入派工数据接口结束...");
        return result;
    }


    @ApiOperation("插入派工数据——根据日计划获取机检一级修计划并派工")
    @GetMapping("/setTaskAllotDataByDayPlanId")
    @ResponseBody
    @BussinessLog(value = "插入派工数据——根据日计划获取机检一级修计划并派工", key = "/xzyMTaskallot/setTaskAllotDataByDayPlanId")
    public RestResult setTaskAllotDataByDayPlanId(@RequestParam("dayPlanId") String dayPlanId) {
        logger.info("/xzyMTaskallot/setTaskAllotDataByDayPlayId----开始调用中台插入派工数据——根据日计划获取机检一级修计划并派工接口...");
        RestResult result = RestResult.success();
        try {
            if (StringUtils.isBlank(dayPlanId)) {
                logger.info("/xzyMTaskallot/setTaskAllotDataByDayPlanId----调用中台插入派工数据——根据日计划获取机检一级修计划并派工接口失败：日计划参数为空...");
                throw new RuntimeException("失败：日计划不能为空!");
            } else {
                boolean flag = xzyMTaskAllotService.setTaskAllotDataByDayPlanId(dayPlanId);
                result.setMsg("成功");
            }
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/setTaskAllotDataByDayPlanId----调用中台插入派工数据——根据日计划获取机检一级修计划并派工接口出错...", ex);
            result = RestResult.fromException(ex, logger, "插入失败");
        }
        logger.info("/xzyMTaskallot/setTaskAllotDataByDayPlanId----调用中台插入派工数据——根据日计划获取机检一级修计划并派工接口结束...");
        return result;
    }

    @ApiOperation("中台插入派工数据(增量保存)")
    @PostMapping("/setTaskAllotDataIncrement")
    @ResponseBody
    @BussinessLog(value = "中台插入派工数据(增量保存)", key = "/xzyMTaskallot/setTaskAllotDataIncrement")
    public RestResult setTaskAllotDataIncrement(@RequestBody List<TaskAllotPacketEntity> taskAllotPacketList) {
        logger.info("/xzyMTaskallot/setTaskAllotDataIncrement----开始调用中台插入派工数据(增量保存)接口...");
        RestResult result = RestResult.success();
        try {
            if (taskAllotPacketList == null || taskAllotPacketList.size() == 0) {
                throw new RuntimeException("失败：无数据，请检查参数!");
            } else {
                boolean flag = xzyMTaskAllotService.setTaskAllotDataIncrement(taskAllotPacketList);
                result.setMsg("插入成功");
            }
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/setTaskAllotDataIncrement----调用中台插入派工数据(增量保存)接口出错...", ex);
            result = RestResult.fromException(ex, logger, "插入失败");
        }
        logger.info("/xzyMTaskallot/setTaskAllotDataIncrement----调用中台插入派工数据(增量保存)接口结束...");
        return result;
    }

}
