package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.QueryTaskAllot;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.*;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * @author gaohan
 * @description 派工任务包管理
 * @createDate 2021/3/15 14:28
 **/
@RestController
@RequestMapping("/xzyMTaskallotpacket")
public class TaskAllotPacketEndpoint {

    protected static final Logger logger = LoggerFactory.getLogger(TaskAllotPacketEndpoint.class);

    private final String repairTaskServiceId = ServiceNameEnum.RepairTaskService.getId();

    @Autowired
    XzyMTaskAllotDeptService taskAllotDeptService;

    @Autowired
    XzyMTaskallotpacketService taskallotpacketService;


    @Autowired
    XzyMTaskcarpartService taskcarpartService;

    @Autowired
    XzyMTaskAllotPersonService taskAllotPersonService;

    @Autowired
    IXzyBTaskallottypeDictService taskallottypeDictService;

    @Autowired
    IXzyBTaskallotshowDictService xzyBTaskallotshowDictService;

    @Autowired
    IXzyCOneallotTemplateService oneallotTemplateService;

    @Autowired
    IXzyCOneallotConfigService xzyCOneallotConfigService;

    @Autowired
    IItemTypeDictService itemTypeDictService;

    @Autowired
    IRemoteService remoteService;

    // private final String repairFaultServiceId = ServiceNameEnum.RepairFaultService.getId();

    @ApiOperation("获取故障信息")
    @RequestMapping(value = "/getFaultData")
    @ResponseBody
    @BussinessLog(value = "获取故障信息", key = "/xzyMTaskallotpacket/getFaultData", type = "04")
    public RestResult getFaultData(@RequestBody JSONObject params) {
        logger.info("准备调用故障信息获取接口...");
        logger.info("接口：getNoAssignFaultForTask，参数：" + params);
        try {
            JSONArray jsonArray = remoteService.getFaultData(params);
            logger.trace("获取到故障信息：" + jsonArray);
            //将已经派了工的数据和已经回写计划了的数据过滤出来，不能重复认领
            //1.获取需要认领的故障集合，择出来故障的id集合
            if (!CollectionUtils.isEmpty(jsonArray)) {
                List<String> faultIds = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!ObjectUtils.isEmpty(jsonObject)) {
                        if (!ObjectUtils.isEmpty(jsonObject.getString("faultId"))) {
                            faultIds.add(jsonObject.getString("faultId"));
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(faultIds)) {
                    //2.获取计划中的故障数据
                    String dayPlanId = params.getString("dayPlanId");
                    String unitCode = ContextUtils.getUnitCode();
                    List<ZtTaskItemEntity> taskItemList = new ArrayList<>();
                    if (StringUtils.isNotBlank(dayPlanId)
                    ) {
                        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, null, "5", "", unitCode);
                        taskItemList = Optional.ofNullable(taskPacketEntityList).orElseGet(ArrayList::new).stream().filter(t -> !CollectionUtils.isEmpty(t.getLstTaskItemInfo())).flatMap(v -> v.getLstTaskItemInfo().stream()).collect(Collectors.toList());
                    }
                    //3.获取派工中的故障数据
                    List<XzyMTaskcarpart> taskcarpartList = taskcarpartService.getCarPartListByItemCodeList(faultIds);
                    if (!CollectionUtils.isEmpty(taskcarpartList) || !CollectionUtils.isEmpty(taskItemList)) {
                        JSONArray resJsonArray = new JSONArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (!ObjectUtils.isEmpty(jsonObject) && !ObjectUtils.isEmpty(jsonObject.getString("faultId"))) {
                                String faultId = jsonObject.getString("faultId");
                                boolean exitAllot = Optional.ofNullable(taskcarpartList).orElseGet(ArrayList::new).stream().anyMatch(t -> t.getItemCode().equals(faultId));
                                boolean exitTask = Optional.ofNullable(taskItemList).orElseGet(ArrayList::new).stream().anyMatch(v -> v.getItemCode().equals(faultId));
                                if (!(exitAllot || exitTask)) {
                                    resJsonArray.add(jsonArray.getJSONObject(i));
                                }
                            }
                        }
                        logger.info("获取故障信息完成");
                        return RestResult.success().setData(resJsonArray);
                    }
                }
            }
            logger.info("获取故障信息完成");
            return RestResult.success().setData(jsonArray);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "调用获取故障信息接口失败，入参：" + params);
        }
    }


    @ApiOperation("查询检修任务")
    @RequestMapping(value = "/getRepairTask")
    @ResponseBody
    @BussinessLog(value = "查询检修任务", key = "/xzyMTaskallotpacket/getRepairTask", type = "04")
    public JSONObject getRepairTask(String unitCode, String dayplanId, String deptCode, String taskAllotType, String mode) {
        JSONObject result = new JSONObject();
        try {
            result = taskallotpacketService.getRepairTask(unitCode, dayplanId, deptCode, taskAllotType, mode);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.put("msg", "操作失败");
            result.put("code", 0);
        }
        return result;
    }

    @ApiOperation("获取派工查询条件")
    @RequestMapping(value = "/getQuery")
    @ResponseBody
    @BussinessLog(value = "获取派工查询条件", key = "/xzyMTaskallotpacket/getQuery", type = "04")
    public JSONObject getQuery(String unitCode, String dayplanId, String deptCode, String taskAllotType, String mode) {
        JSONObject result = new JSONObject();
        try {
            JSONObject query = taskallotpacketService.getQuery(unitCode, dayplanId, deptCode, taskAllotType, mode);
            result.put("data", query);
            result.put("msg", "获取成功");
            result.put("code", 1);
        } catch (Exception e) {
            logger.error("/xzyMTaskallotpacket/getQuery获取派工查询条件接口出错：" + e);
            result.put("msg", "获取失败");
            result.put("code", 0);
        }
        return result;
    }

    /**
     * @author: duanzefan
     * @Date: 2020/9/20 17:06
     * @Description: 新增派工信息
     */
    @ApiOperation("新增派工信息")
    @RequestMapping(value = "/setTaskAllot")
    @BussinessLog(value = "新增派工信息", key = "/xzyMTaskallotpacket/setTaskAllot", type = "04")
    public RestResult setTaskAllot(@RequestBody Map<String, Object> map) {
        logger.info("/xzyMTaskallot/setTaskAllotData----开始调用新增派工信息接口...");
        RestResult result = RestResult.success();
        try {
            taskallotpacketService.setTaskAllot(map);
            result.setMsg("派工成功");
            logger.info("/xzyMTaskallot/setTaskAllotData----调用新增派工信息接口结束...");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/setTaskAllotData----调用新增派工信息接口出错...", ex);
            result = RestResult.fromException(ex, logger, "派工失败");
        }
        return result;
    }

    /**
     * @Description: 派工查询接口
     */
    @ApiOperation("派工查询接口")
    @PostMapping(value = "/getQueryTaskAllotList")
    @ResponseBody
    public RestResult getQueryTaskAllotList(@RequestBody JSONObject jsonObject) {
        logger.info("getQueryTaskAllotList----开始调用派工查询接口...");
        RestResult result = RestResult.success();
        try {
            //获取数据
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            List<QueryTaskAllot> queryTaskAllotList = taskallotpacketService.getQueryTaskAllotList(jsonObject);
            logger.info("getQueryTaskAllotList----调用派工查询接口正常结束...");
            return RestResult.success().setData(CommonUtils.getPage(queryTaskAllotList, pageNum, pageSize));
        } catch (Exception ex) {
            logger.error("getQueryTaskAllotList----派工查询接口出错...", ex);
            return RestResult.fromException(ex, logger, "派工查询接口");
        }
    }

    /**
     * @author: 冯帅
     */
    @ApiOperation("获取项目类型")
    @RequestMapping(value = "/getItemType")
    @BussinessLog(value = "获取项目类型", key = "/xzyMTaskallotpacket/getItemType", type = "04")
    public RestResult getItemType() {
        logger.info("/xzyMTaskallot/getItemType----开始调用新增派工信息接口...");
        RestResult result = RestResult.success();
        try {
            List<ItemTypeDict> itemTypeDictList = MybatisPlusUtils.selectList(
                itemTypeDictService,
                eqParam(ItemTypeDict::getFlag, "1")
            );
            result.setData(itemTypeDictList);
            result.setMsg("获取成功");
            logger.info("/xzyMTaskallot/getItemType----调用获取项目类型接口结束...");
        } catch (Exception ex) {
            logger.error("/xzyMTaskallot/getItemType----调用获取项目类型接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        return result;
    }


    /**
     * @author: duanzefan
     * @Date: 2020/10/20 17:44
     * @Description: 获取故障包
     */
    @ApiOperation("获取故障包")
    @RequestMapping(value = "/getFaultPacket")
    @ResponseBody
    @BussinessLog(value = "获取故障包", key = "/xzyMTaskallotpacket/getFaultPacket", type = "04")
    public JSONObject getFaultPacket() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> instr = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> page = new HashMap<>();
        Map<String, Object> datas = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("params", params);
        paramMap.put("instr", instr);
        paramMap.put("user", user);
        paramMap.put("page", page);
        paramMap.put("datas", datas);
        JSONObject param = new JSONObject(paramMap);
        JSONObject faultPacket = new JSONObject();
        logger.info("准备调用获取故障包接口...");
        logger.info("接口：getFaultPacket");
        try {
            faultPacket = new RestRequestKitUseLogger<JSONObject>(repairTaskServiceId, logger) {
            }.postObject("/packet/getFaultPacket", param);
            // String url = RestKit.getServicePath("repairtaskservice") + "/packet/getFaultPacket";
            // faultPacket = RestKit.getRestTemplate().postForObject(url, param.toString(), JSONObject.class);
        } catch (Exception e) {
            logger.info("调用获取故障包接口失败", e);
        }
        logger.trace("获取到故障包：" + faultPacket);
        return faultPacket;
    }


    /** 前端不需要调用这个接口，暂时注释掉 2022-03-07
     @ApiOperation("增加派工包")
     @RequestMapping(value = "/setTaskAllotPackets")
     @ResponseBody
     @BussinessLog(value = "增加派工包", key = "/xzyMTaskallotpacket/setTaskAllotPackets", type = "04")
     public int setTaskAllotPackets(@RequestBody List<XzyMTaskallotpacket> taskallotpackets) {
     int res = 0;
     try {
     taskallotpacketService.setTaskAllotPackets(taskallotpackets);
     } catch (Exception e) {
     logger.error(e.getMessage());
     logger.error(e.toString());
     }
     return res;
     }**/

    /** 暂时注释掉 2022-03-07
     @ApiOperation("获取任务部件辆序")
     @RequestMapping(value = "/getXzyMTaskcarparts")
     @ResponseBody
     @BussinessLog(value = "获取任务部件辆序", key = "/xzyMTaskallotpacket/getXzyMTaskcarparts", type = "04")
     public List<XzyMTaskcarpart> getXzyMTaskcarparts(@RequestBody Map<String, String> queryCarPartMap) {
     // xzy_m_taskcarpart 按照 dayPlanID unitCode deptCode 查询 目标删除数据

     return taskcarpartService.getCarPartLists(queryCarPartMap);
     }**/

    /** 暂时注释掉 2022-03-07
     // 根据作业包中的车组id查询车组履历，获取编组数量
     public String getTrainsetDetialInfo(String trainsetID) {
     logger.info("准备调用履历接口获取编组数量...");
     logger.info("接口：getTrainsetDetialInfo，参数：trainsetid = " + trainsetID);
     logger.info("调用履历接口获取车组信息完成...");
     TrainsetInfo trainsetInfo = remoteService.getTrainsetDetialInfo(trainsetID);
     String marshalcount = trainsetInfo.getIMarshalcount();
     logger.trace("根据车组id获取履历,获取到编组数量：" + marshalcount);
     logger.info("获取编组数量完成");
     return marshalcount;
     }**/

    /** 暂时注释掉 2022-03-07
     @ApiOperation("获取根据条件获取获取派工部门")
     @RequestMapping(value = "/getTaskAllotDepts")
     @ResponseBody
     @BussinessLog(value = "获取根据条件获取派工部门", key = "/xzyMTaskallotpacket/getTaskAllotDepts", type = "04")
     public List<XzyMTaskallotdept> getTaskAllotDepts(@RequestBody Map<String, String> map) {
     return taskAllotDeptService.getTaskAllotDepts(map);
     }**/

    /** 暂时注释掉 2022-03-07
     @ApiOperation("获取根据条件获取派工人员岗位")
     @RequestMapping(value = "/getPersonPosts")
     @ResponseBody
     @BussinessLog(value = "获取根据条件获取派工人员岗位", key = "/xzyMTaskallotpacket/getPersonPosts", type = "04")
     public List<TaskAllotPersonPostEntity> getPersonPosts(@RequestBody List<String> personids) {
     return taskAllotPersonService.getPersonPosts(personids);
     }**/

    /** 暂时注释掉 2022-03-07
     @ApiOperation("获取根据条件获取任务包")
     @RequestMapping(value = "/getTaskAllotPackets")
     @ResponseBody
     @BussinessLog(value = "获取根据条件获取任务包", key = "/xzyMTaskallotpacket/getTaskAllotPackets", type = "04")
     public List<XzyMTaskallotpacket> getTaskAllotPackets(@RequestBody Map<String, String> queryCarPartMap) {
     return taskallotpacketService.getTaskAllotPackets(queryCarPartMap);
     }**/


}
