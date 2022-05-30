package com.ydzbinfo.emis.trainRepair.common.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.configs.TrainRepairProperties;
import com.ydzbinfo.emis.trainRepair.common.model.PostAndRole;
import com.ydzbinfo.emis.trainRepair.common.model.Temple;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.common.model.WorkTeam;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultPacket;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.RemainFaultNewVO;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.TreeModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.HighLevelRepair;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetHotSpareInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration.TreePartNodeModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.RecheckPacket;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ShuntPlanModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.RuntimeRole;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowRunService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.BasicFlowTypeEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPostService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskallotpacketService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.PriData;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.UnitLevelRepairInfo;
import com.ydzbinfo.emis.trainRepair.workprocess.model.WorkTime;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.result.RestResultGeneric;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/15 14:21
 **/
@RestController
@RequestMapping({"common", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/common"})
public class CommonController {


    protected static final Logger logger = LoggerFactory.getLogger(CommonController.class);
    @Resource
    IRemoteService remoteService;

    @Autowired
    private IXzyCWorkcritertionService xzyCWorkcritertionService;

    @Autowired
    IRepairMidGroundService midGroundService;

    @Autowired
    XzyMTaskallotpacketService xzyMTaskallotpacketService;

    //用户信息服务
    private final String UserServiceId = ServiceNameEnum.UserService.getId();


    @Resource
    IExtraFlowTypeService extraFlowTypeService;

    @Autowired
    IFlowRunService flowRunService;

    @Autowired
    IPostService postService;


    /**
     * 获取日计划编号
     */
    @RequestMapping(value = "/getDay")
    @ResponseBody
    public Map<String, Object> getDay(String unitCode) {
        Map<String, Object> result = new HashMap<>();
        try {
            String dayplanId = DayPlanUtil.getDayPlanId(unitCode);
            //获取用户班组
            List<Temple> workTeamList = new ArrayList<>();

            User user = UserUtil.getUserInfo();
            if (user.getWorkTeam() != null) {
                Temple t1 = new Temple();
                t1.setId(user.getWorkTeam().getTeamCode());
                t1.setName(user.getWorkTeam().getTeamName());
                workTeamList.add(t1);
            }
            result.put("dayPlanId", dayplanId);
            result.put("workGroup", workTeamList);
            result.put("code", 1);
            result.put("msg", "成功");
            return result;
        } catch (Exception ex) {
            logger.error("获取日计划编号失败", ex);
            result.put("code", 0);
            result.put("rows", null);
            result.put("msg", "失败");
        }
        return result;
    }

    @Autowired
    private TrainRepairProperties trainRepairProperties;

    /**
     * 获取作业班组列表 unitCode 运用所编码
     */
    @GetMapping("/getWorkTeamsByUnitCode")
    @ResponseBody
    @ApiOperation("获取作业班组列表(检修班组)")
    public Map getWorkTeamsByUnitCode(String unitCode) {
        List<Temple> workTeamList = remoteService.getTramsByUnitCodeAndDeptType(unitCode, trainRepairProperties.getWorkTeamTypeCode());
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (workTeamList != null && workTeamList.size() > 0) {
                map.put("code", 1);
                workTeamList.sort(Comparator.comparing(Temple::getName));
                map.put("rows", workTeamList);
                map.put("msg", "成功");
            } else {
                map.put("rows", null);
                map.put("code", 0);
                map.put("msg", "失败");
            }
            return map;
        } catch (Exception e) {
            map.put("rows", null);
            map.put("code", 0);
            map.put("msg", e.getMessage());
            return map;
        }
    }

    @ApiOperation(value = "获取作业班组列表(所有班组)")
    @GetMapping("/getAllTeamsByUnitCode")
    public RestResult getAllTeamsByUnitCode(String unitCode) {
        try {
            List<Temple> teamList = remoteService.getTramsByUnitCodeAndDeptType(unitCode, null);
            return RestResult.success().setData(teamList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取作业班组列表");
        }
    }

    @ApiOperation(value = "获取出入所车组")
    @GetMapping("/getTrainsetListReceived")
    public RestResult getTrainsetListReceived(String unitCode) {
        try {
            List<TrainsetBaseInfo> processCarPartEntityList = remoteService.getTrainsetListReceived(unitCode);
            return RestResult.success().setData(processCarPartEntityList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取出入所车组错误");
        }
    }

    @ApiOperation(value = "获取出入所车组——2j")
    @GetMapping("/getTrainsetNameListRepair")
    public RestResult getTrainsetNameListRepair() {
        try {
            String unitCode = ContextUtils.getUnitCode();
            List<TrainsetBaseInfo> processCarPartEntityList = remoteService.getTrainsetNameListRepair(unitCode);
            return RestResult.success().setData(processCarPartEntityList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取出入所车组错误");
        }
    }

    @ApiOperation(value = "获取所有车组")
    @GetMapping("/getTrainsetList")
    public RestResult getTrainsetList() {
        try {
            List<TrainsetBaseInfo> processCarPartEntityList = remoteService.getTrainsetList();
            return RestResult.success().setData(processCarPartEntityList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取出入所车组错误");
        }
    }

    @ApiOperation(value = "获取其他车组(热备车)")
    @GetMapping("/getTrainsetHotSpareInfo")
    public RestResult getTrainsetHotSpareInfo() {
        try {
            List<TrainsetHotSpareInfo> trainsetHotSpareInfos = remoteService.getTrainsetHotSpareInfo();
            return RestResult.success().setData(trainsetHotSpareInfos);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取其他车组(热备车)错误");
        }
    }

    @ApiOperation(value = "高级修车辆")
    @GetMapping("/getTrainsetInHighLevelRepair")
    public RestResult getTrainsetInHighLevelRepair() {
        try {
            List<HighLevelRepair> trainsetHotSpareInfos = remoteService.getTrainsetInHighLevelRepair();
            return RestResult.success().setData(trainsetHotSpareInfos);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取其他车组(热备车)错误");
        }
    }

    @ApiOperation(value = "获取热备车和高级修车辆")
    @GetMapping("/getTrainsetHotSpareInfoAndHighLevelRepair")
    public RestResult getTrainsetHotSpareInfoAndHighLevelRepair() {
        try {
            List<TrainsetHotSpareInfo> trainsetHotSpareInfos = remoteService.getTrainsetHotSpareInfoAndHighLevelRepair();
            return RestResult.success().setData(trainsetHotSpareInfos);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取热备车和高级修车辆");
        }
    }


    @ApiOperation(value = "获取故障列表")
    @GetMapping("/getFaultDataByIdList")
    public RestResult getFaultDataByIdList(String itemCode, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        try {
            List<String> itemCodes = new ArrayList<>();
            if (StringUtils.isNotBlank(itemCode)) {
                itemCodes = Arrays.asList(itemCode.split(","));
            }
            List<RemainFaultNewVO> remainFaultNewVOS = remoteService.getFaultDataByIdList(itemCodes);
            return RestResult.success().setData(CommonUtils.getPage(remainFaultNewVOS, pageNum, pageSize));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取故障列表错误");
        }
    }

    @ApiOperation(value = "重联车型选择")
    @GetMapping("/getTrainTypeList")
    public RestResult getTrainTypeList() {
        try {
            JSONObject trainTypes = remoteService.getTrainTypeList();
            return RestResult.success().setData(trainTypes);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "重联车型选择错误");
        }
    }

    @ApiOperation(value = "获取开行信息")
    @GetMapping("/getRunRoutingDataByDate")
    public RestResult getRunRoutingDataByDate(String date, String trainsetId, String planFlag, String pageNum, String pageSize) {
        try {
            List<PriData> priDataInfoList = remoteService.getRunRoutingDataByDate(date, trainsetId, planFlag);
            if (StringUtils.isBlank(pageNum)) {
                return RestResult.success().setData(priDataInfoList);
            }
            return RestResult.success().setData(CommonUtils.getPage(priDataInfoList, Integer.parseInt(pageNum), Integer.parseInt(pageSize)));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取开行信息错误");

        }
    }

    @ApiOperation(value = "获取调车计划")
    @GetMapping("/getShuntingPlanByCondition")
    //beginDate,endDate, deptCode运用所编码, emuId车组ID
    public RestResult getShuntingPlanByCondition(String deptCode, String emuId, String beginDate, String endDate) {
        try {
            List<ShuntPlanModel> shuntPlanModels = remoteService.getShuntingPlanByCondition(deptCode, emuId, beginDate, endDate);
            return RestResult.success().setData(shuntPlanModels);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取调车计划错误");
        }
    }


    @ApiOperation(value = "获取检修列表")
    @GetMapping("/getPgMPacketrecordList")
    public RestResult getPgMPacketrecordList(String trainsetName, String trainsetId) {
        try {
            List<UnitLevelRepairInfo> packetRecords = remoteService.getPgMPacketrecordList(trainsetName, trainsetId);
            packetRecords.sort(Comparator.comparing(UnitLevelRepairInfo::getMainCycCode));
            return RestResult.success().setData(packetRecords);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取检修列表错误");
        }
    }

    @ApiOperation(value = "获取车组详情(长编短编)")
    @GetMapping("/getTrainsetDetialInfo")
    //0短编 1长编
    public RestResult getTrainsetDetialInfo(String trainsetId) {
        try {
            int marshalCount = 0;
            TrainsetInfo trainsetInfo = remoteService.getTrainsetDetialInfo(trainsetId);
            if (Integer.parseInt(trainsetInfo.getIMarshalcount()) > 8) {
                marshalCount = 1;
            } else {
                marshalCount = 0;
            }
            return RestResult.success().setData(marshalCount);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取车组详情错误");
        }
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/13
     * @Description: 根据日计划获取检修任务
     */
    @ApiOperation(value = "获取检修任务")
    @GetMapping("/getPacketTaskByDayplanId")
    public RestResult getPacketTaskByDayplanId(String dayPlanId, String deptCode) {
        logger.info("/common/getPacketTaskByDayplanId----开始调用获取检修任务接口...");
        try {
            List<ZtTaskPacketEntity> ztTaskPacketEntityList = remoteService.getPacketTaskByDayplanId(dayPlanId, deptCode);
            return RestResult.success().setData(ztTaskPacketEntityList).setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/common/getPacketTaskByDayplanId----调用获取检修任务接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }

    @ApiOperation(value = "获取车型")
    @GetMapping("/getTraintypeListLocal")
    public RestResult getTraintypeListLocal() {
        try {
            String unitCode = ContextUtils.getUnitCode();
            List<String> trainTypes = remoteService.getTraintypeListLocal(unitCode);
            return RestResult.success().setData(trainTypes);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取车型报错");
        }

    }

    @ApiOperation(value = "获取车型下的批次")
    @GetMapping("/getPatchListByTraintype")
    public RestResult getPatchListByTraintype(String trainType) {
        try {
            List<String> templateId = remoteService.getPatchListByTraintype(trainType);
            return RestResult.success().setData(templateId);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取车型下的批次错误");
        }
    }

    @ApiOperation(value = "获取辆序")
    @GetMapping("/getCarNoListByTrainType")
    public RestResult getCarNoListByTrainType(String trainType) {
        try {
            List<String> list = TrainsetUtils.generateCarNoListFromMarshalCount(
                remoteService.getMarshalCountByTrainType(trainType)
            );
            return RestResult.success().setData(list);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取辆序错误");
        }
    }

    @ApiOperation(value = "获取检修项目")
    @PostMapping("/selectRepairItemListByCarNoParam")
    public RestResult selectRepairItemListByCarNoParam(@RequestBody Map map) {
        //platform平台 trainType车型(必填) trainBatch批次(必填) carNoList辆序
        logger.info("获取检修项目入参" + map);
        try {
            //获取派工配置二级修是否启用新项目
            boolean useNewItem = ConfigUtil.isUseNewItem();
            List<RepairItemVo> repairItemVos = new ArrayList<>();
            if (useNewItem) {
                repairItemVos = remoteService.selectRepairItemListByCarNoParam(map);
            } else {
                Page page = new Page(1, Integer.MAX_VALUE);
                repairItemVos = xzyMTaskallotpacketService.selectRepairItemList(map, page);
            }
            return RestResult.success().setData(repairItemVos);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取检修项目错误");
        }

    }

    @ApiOperation(value = "获取id")
    @GetMapping("/getNewId")
    public RestResult getNewId(@RequestParam(value = "number", defaultValue = "1") int number) {
        try {
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                ids.add(UUID.randomUUID().toString());
            }
            return RestResult.success().setData(ids);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取新id错误");
        }

    }

    /**
     * @author: 冯帅
     * @return: true:段级  false:所级
     */
    @ApiOperation("获取当前部署等级")
    @GetMapping("/getIsCenter")
    @ResponseBody
    public RestResult getIsCenter() {
        try {
            logger.info("/common/getIsCenter---开始调用获取当前部署等级接口");
            boolean isCenter = ContextUtils.isCenter();
            return RestResult.success().setData(isCenter).setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/common/getIsCenter----调用获取当前部署等级接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        } finally {
            logger.info("/common/getIsCenter----调用获取当前部署等级接口结束...");
        }
    }

    @ApiOperation("获取当前用户动车段下的所 或 当前所 下拉列表")
    @GetMapping("/getUnitList")
    @ResponseBody
    public RestResult getUnitList() {
        logger.info("/common/getUnitList---开始调用当前用户动车段下的所或当前所下拉列表接口");
        try {
            return RestResult.success().setData(remoteService.getUnitList());
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取当前用户动车段下的所 或 当前所 下拉列表错误");
        }
    }

    @ApiOperation(value = "获取岗位+角色下拉框")
    @GetMapping(value = "/getPostRoleList")
    @ResponseBody
    public RestResultGeneric<List<PostAndRole>> getPostRoleList() {
        logger.info("/xzyCWorkcritertion/getPostRoleList----开始调用获取岗位+角色下拉框接口...");
        try {
            return RestResultGeneric.success(postService.getPostAndRoleList()).setMsg("获取成功");
        } catch (Exception e) {
            logger.error("/xzyCWorkcritertion/getPostRoleList----调用获取岗位+角色下拉框接口出错...", e);
            return RestResultGeneric.fromException(e, logger, "获取失败");
        } finally {
            logger.info("/xzyCWorkcritertion/getPostRoleList----调用获取岗位+角色下拉框接口结束...");
        }
    }

    @ApiOperation(value = "获取服务是否是段级系统")
    @GetMapping("/isCenter")
    public RestResult isCenter() {
        try {
            return RestResult.success().setData(ContextUtils.isCenter());
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取服务是否是段级系统失败");
        }
    }

    @ApiOperation(value = "获取当前部署所编码")
    @GetMapping("/getUnitCode")
    public RestResult getUnitCode() {
        try {
            return RestResult.success().setData(ContextUtils.getUnitCode());
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取当前部署所编码失败");
        }
    }

    @ApiOperation(value = "获取当前部署所名称")
    @GetMapping("/getUnitName")
    public RestResult getUnitName() {
        try {
            return RestResult.success().setData(ContextUtils.getUnitName());
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取当前部署所名称失败");
        }
    }

    @ApiOperation(value = "获取派工岗位信息")
    @GetMapping("/getTaskPostList")
    public RestResult getTaskPostList(String unitCode, String dayPlanId, String staffId, String trainsetId) {
        try {
            List<RuntimeRole> runtimeRoles = flowRunService.getTaskPostList(dayPlanId, unitCode, staffId, trainsetId);
            return RestResult.success().setData(runtimeRoles);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取派工岗位信息错误");
        }
    }

    @ApiOperation(value = "获取功能分类")
    @GetMapping("/getFunctionClass")
    public RestResult getFunctionClass() {
        try {
            List<TreeModel> treeModels = remoteService.getFunctionClass();
            return RestResult.success().setData(treeModels);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取功能分类错误");
        }
    }

    @ApiOperation(value = "获取部件(构型)节点编码")
    @GetMapping("/getBatchBomNodeCode")
    public RestResult getBatchBomNodeCode(String trainsetId, String carNoListStr) {
        try {
            List<TreePartNodeModel> batchBomNodeEntities = remoteService.getBatchBomNodeCode(trainsetId, carNoListStr);
            return RestResult.success().setData(batchBomNodeEntities);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取部件(构型)节点编码失败");
        }
    }

    /**
     * @author: 冯帅
     */
    @ApiOperation(value = "根据班次获取工作时间")
    @GetMapping("/getWorkTimeByDayPlanId")
    public RestResult getWorkTimeByDayPlanId(@RequestParam("dayPlanId") String dayPlanId) {
        logger.info("/common/getWorkTimeByDayPlanId----开始调用根据班次获取工作时间接口...");
        try {
            WorkTime workTime = DayPlanUtil.getWorkTimeByDayPlanId(dayPlanId);
            return RestResult.success().setData(workTime).setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/common/getWorkTimeByDayPlanId----调用根据班次获取工作时间出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        } finally {
            logger.info("/common/getWorkTimeByDayPlanId----调用根据班次获取工作时间接口结束...");
        }
    }

    @ApiOperation(value = "获取班组")
    @GetMapping("/getWorkTeam")
    public RestResult getWorkTeam() {
        try {
            WorkTeam workTeam = UserUtil.getUserInfo().getWorkTeam();
            return RestResult.success().setData(workTeam);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取班组");
        }
    }


    @GetMapping("/getUser")
    @ResponseBody
    @ApiOperation("获取用户信息")
    public RestResult getUser() {
        try {
            return RestResult.success().setData(UserUtil.getUserInfo());
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取用户失败");
        }
    }

    @ApiOperation("获取轮对受电弓复核包")
    @GetMapping("/getRecheckPacket")
    public RestResult getRecheckPacket() {
        try {
            RecheckPacket recheckPacket = remoteService.getRecheckPacket();
            return RestResult.success().setData(recheckPacket);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取用户失败");
        }
    }

    @ApiOperation("获取故障包")
    @GetMapping("/getFaultPacket")
    public RestResult getFaultPacket() {
        try {
            FaultPacket faultPacket = remoteService.getFaultPacket();
            return RestResult.success().setData(faultPacket);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取用户失败");
        }
    }

    @ApiOperation("获取班组列表(包含兼职)")
    @GetMapping("/getActuallyWorkTeams")
    public RestResult getActuallyWorkTeams(@RequestParam String unitCode, @RequestParam String dayPlanId, String[] trainsetIds, @RequestParam String staffId) {
        try {
            List<WorkTeam> workTeams = flowRunService.getActuallyWorkTeams(unitCode, dayPlanId, trainsetIds, staffId);
            return RestResult.success().setData(workTeams);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取班组列表(包含兼职)失败");
        }
    }

    @ApiOperation("从计划中获取股道信息")
    @GetMapping("/getTrackAreaByDept")
    public RestResult getTrackAreaByDept(@RequestParam("unitCode") String unitCode) {
        try {
            List<ZtTrackAreaEntity> trackAreaEntityList = CacheUtil.getDataUseThreadCache(
                "remoteService.getTrackAreaByDept_" + unitCode,
                () -> remoteService.getTrackAreaByDept(unitCode)
            );
            return RestResult.success().setData(trackAreaEntityList);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "从计划中获取股道信息失败");
        }
    }


}
