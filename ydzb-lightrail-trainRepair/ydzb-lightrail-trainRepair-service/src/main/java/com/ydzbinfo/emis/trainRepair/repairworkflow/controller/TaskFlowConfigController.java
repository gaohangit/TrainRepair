package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfoDispose;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryFlowModelGeneral;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowUtil;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.RepairTypeEnum;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.RepairWorkFlowConstants;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.upload.UpLoadFileUtils;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gaohan
 * @description 作业流程配置
 * @createDate 2021/4/9 9:34
 **/
@RestController
@RequestMapping({"taskFlowConfig", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/taskFlowConfig"})
public class TaskFlowConfigController extends BaseController {
    @Resource
    IFlowService flowService;

    // TODO 查询流程配置列表和查询配置详情没有分开，待优化
    @ApiOperation("查询作业流程配置")
    @GetMapping(value = "getTaskFlowConfigList")
    public Object getTaskFlowConfigList(String flowName, String flowTypeCode, String trainType, String trainTemplate, String trainsetId, String unitCode, String usable, Integer pageNum, Integer pageSize) {
        try {
            Set<String> flowTypeCodes = new HashSet<>();
            if (StringUtils.isNotBlank(flowTypeCode)) {
                flowTypeCodes.add(flowTypeCode);
            }
            QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
            queryFlowModelGeneral.setFlowName(flowName);
            queryFlowModelGeneral.setFlowTypeCodes(flowTypeCodes.toArray(new String[0]));
            queryFlowModelGeneral.setUnitCode(unitCode);
            queryFlowModelGeneral.setUsable(usable);
            List<FlowInfo> flowInfoList = flowService.getFlowInfoList(queryFlowModelGeneral);
            // 根据车组条件过滤流程
            List<FlowInfo> filteredFlowInfoList = FlowUtil.filterFlowInfoListByTrainInfo(flowInfoList, trainType, trainTemplate, trainsetId);
            for (FlowInfo flowInfo : filteredFlowInfoList) {
                // 将排除条件反转
                FlowUtil.convertToNotExclude(flowInfo);
                // 排序节点
                flowInfo.setNodes(FlowUtil.getSortedNodes(flowInfo));
            }

            if (pageNum != null && pageSize != null) {
                return RestResult.success().setData(CommonUtils.getPage(filteredFlowInfoList, pageNum, pageSize));
            } else {
                return RestResult.success().setData(filteredFlowInfoList);
            }
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询作业流程配置错误");
        }
    }


    @ApiOperation("修改流程配置")
    @PostMapping(value = "setTaskFlowConfig")
    public Object setTaskFlowConfig(@RequestBody FlowInfoDispose flowInfoDispose) {
        try {
            IFlowService.SetTaskFlowConfigResult result = flowService.setTaskFlowConfig(flowInfoDispose, false);
            return RestResult.success().setData(result);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改流程配置错误");
        }
    }

    @ApiOperation("设置默认流程")
    @GetMapping(value = "setDefaultFlowConfig")
    public Object setDefaultFlowConfig(@RequestParam String flowId) {
        try {
            String msg = flowService.setDefaultFlowConfig(flowId);
            Map<String, Object> map = new HashMap<>();
            map.put("resultInfo", msg);
            return RestResult.success().setData(map);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "设置默认流程错误");
        }
    }

    @ApiOperation("根据id获取流程配置")
    @GetMapping(value = "getFlowInfoById")
    public Object getFlowInfoById(@RequestParam String flowId) {
        try {
            FlowInfo flowInfo = flowService.getFlowInfoByFlowId(flowId);
            return RestResult.success().setData(flowInfo);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "根据id获取流程配置错误");
        }
    }

    @ApiOperation("修改为已发布状态")
    @GetMapping(value = "setFlowUsable")
    public Object setFlowUsable(@RequestParam String flowId) {
        try {
            flowService.setFlowUsable(flowId);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改为已发布状态错误");
        }
    }

    @ApiOperation("删除流程配置")
    @PostMapping(value = "delTaskFlowConfig")
    public Object delTaskFlowConfig(@RequestBody FlowInfo flowInfo) {
        try {
            return RestResult.success().setData(flowService.deleteFlowInfoAndRepairConditionDefaultType(flowInfo));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "删除流程配置错误");
        }
    }
    @ApiOperation("删除流程配置前提示信息")
    @GetMapping(value = "getDelTaskFlowConfigWarningInfo")
    public Object delTaskFlowConfigWarningInfo(@RequestParam String  flowId) {
        try {
            return RestResult.success().setData(flowService.getDelTaskFlowConfigWarningInfo(flowId));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "删除流程配置前提示信息错误");
        }
    }

    @ApiOperation("获取检修类型")
    @GetMapping(value = "getRepairTypes")
    public Object getRepairTypes() {
        try {
            return RestResult.success().setData(
                Stream.of(
                    RepairTypeEnum.PersonRepair,
                    RepairTypeEnum.MachineRepair
                ).map(EnumUtils::toMap).collect(Collectors.toList())
            );
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取检修类型错误");
        }
    }

    @ApiOperation("图片上传")
    @PostMapping(value = "uploadFile")
    public Object uploadFile(MultipartHttpServletRequest multipartHttpServletRequest) {
        try {
            String dateDirName = DateTimeUtil.format(new Date(), Constants.DEFAULT_DATE_FORMAT);
            String dirPrefix = RepairWorkFlowConstants.UPLOAD_PATH + "/" + RepairWorkFlowConstants.FLOW_CONFIG_MODULE_NAME + "/" + dateDirName;
            List<UploadedFileInfo> uploadedFileInfos = UpLoadFileUtils.uploadImages(multipartHttpServletRequest, dirPrefix + "/images");
            return RestResult.success().setData(uploadedFileInfos);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "图片上传错误");
        }
    }

    @ApiOperation("校验流程配置")
    @GetMapping(value = "verifyFlowType")
    public Object verifyFlowType(String unitCode) {
        try {
            flowService.verifyFlowType(unitCode);
            return RestResult.success().setMsg("校验成功");
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "校验流程配置错误");
        }
    }


    @ApiOperation("获取某刷卡页面下的作业流程类型名称")
    @GetMapping(value = "getFlowByFlowPageCodeAndFlowTypeCode")
    public Object getFlowTypes(@RequestParam String unitCode, String flowTypeCode, @RequestParam(value = "queryPastFlow", defaultValue = "true") boolean queryPastFlow, String flowPageCode) {
        try {
            List<FlowWithExtraInfo> flows = flowService.getFlowByFlowTypeCode(unitCode, flowTypeCode, queryPastFlow, flowPageCode);
            return RestResult.success().setData(flows);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取某刷卡页面下的作业流程类型");
        }
    }
}
