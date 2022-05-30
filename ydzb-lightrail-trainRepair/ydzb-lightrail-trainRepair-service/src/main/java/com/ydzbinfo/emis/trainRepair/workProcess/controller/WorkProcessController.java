package com.ydzbinfo.emis.trainRepair.workProcess.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.common.userutil.UserUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.IXzyCRfidRegistService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotDeptService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskallotpacketService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskcarpartService;
import com.ydzbinfo.emis.trainRepair.trackPowerInfo.service.TrackPowerInfoService;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.service.TrainsetPostionService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IProcessTimeRecordService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IRfidCardSummaryService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IWorkProcessService;
import com.ydzbinfo.emis.trainRepair.workprocess.model.*;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.OrderBy;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.upload.UpLoadFileUtils;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfo;
import com.ydzbinfo.hussar.system.bsp.organ.SysStaff;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;


/**
 * @author:冯帅
 * @Date: 2021/5/8
 * @Description: 作业过程管理
 */
@Api(description = "作业过程管理")
@Controller
@RequestMapping("/workProcess")
public class WorkProcessController extends BaseController {

    @Autowired
    IWorkProcessService workProcessService;

    @Resource
    IRemoteService iRemoteService;

    @Autowired
    IRfidCardSummaryService rfidCardSummaryService;

    @Autowired
    XzyMTaskallotpacketService xzyMTaskallotpacketService;

    @Autowired
    XzyMTaskcarpartService xzyMTaskcarpartService;

    @Autowired
    TrainsetPostionService trainsetPostionService;

    @Autowired
    TrackPowerInfoService trackPowerInfoService;

    @Autowired
    IXzyCWorkcritertionService xzyCWorkcritertionService;

    @Autowired
    XzyMTaskAllotDeptService xzyMTaskAllotDeptService;

    @Autowired
    IProcessTimeRecordService IProcessTimeRecordService;

    @Autowired
    IXzyCRfidRegistService xzyCRfidregistService;

    @Value("${server.port}")
    private String port;

    /**-------------------------------------------一级修-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 查询一级修列表
     */
    @ApiOperation(value = "查询一级修列表", notes = "查询一级修列表")
    @PostMapping(value = "/getOneWorkProcessList")
    @ResponseBody
    public RestResult getOneWorkProcessList(@RequestBody QueryOneWorkProcessData queryOneWorkProcessData) {
        logger.info("/workProcess/getOneWorkProcessList----开始调用查询一级修列表接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            Integer pageNum = queryOneWorkProcessData.getPageNum();
            Integer pageSize = queryOneWorkProcessData.getPageSize();
            List<QueryOneWorkProcessData> queryOneWorkProcessDataList = workProcessService.getOneWorkProcessList(queryOneWorkProcessData);
            JSONObject resObj = new JSONObject();
            resObj.put("queryOneWorkProcessDataList", CommonUtils.getPage(queryOneWorkProcessDataList, pageNum, pageSize == 0 ? 30 : pageSize).getRecords());
            resObj.put("count", queryOneWorkProcessDataList.size());
            result.setData(resObj);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getOneWorkProcessList----调用查询一级修列表出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/workProcess/getOneWorkProcessList----调用查询一级修列表接口结束...");
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 添加一级修作业过程
     */
    @ApiOperation(value = "添加一级修作业过程", notes = "添加一级修作业过程")
    @RequestMapping(value = "/addOneWorkProcess")
    @ResponseBody
    public RestResult addOneWorkProcess(HttpServletRequest req) {
        logger.info("/workProcess/addOneWorkProcess----开始调用添加一级修作业过程接口...");
        RestResult result = RestResult.success();
        try {
            //获取业务数据
            String oneWorkData = req.getParameter("oneWorkData");
            JSONObject jsonObject = JSONObject.parseObject(oneWorkData);
            OneWorkProcessData oneWorkProcessData = JSONObject.toJavaObject(jsonObject, OneWorkProcessData.class);
            if (!ObjectUtils.isEmpty(oneWorkProcessData) && ObjectUtils.isEmpty(oneWorkProcessData.getCritertionId())) {
                ShiroUser currentUser = ShiroKit.getUser();
                currentUser.getRemoteAddress();
                logger.info("PC端添加一级修作业过程数据有问题，前端没有传过来作业标准id，操作人的ip地址为：" + currentUser.getRemoteAddress());
                throw new RuntimeException("添加失败,请选择作业项目!");
            }
            //保存图片到服务器
            List<UploadedFileInfo> uploadedFileInfos = UpLoadFileUtils.uploadImages(req, "workProcess");
            //插入业务数据
            boolean flag = workProcessService.addOneWorkProcess(oneWorkProcessData, uploadedFileInfos);
            result.setMsg("添加成功");
        } catch (Exception ex) {
            logger.error("/workProcess/addOneWorkProcess----调用添加一级修作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "添加失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 删除一级修作业过程
     */
    @ApiOperation(value = "删除一级修作业过程", notes = "删除一级修作业过程")
    @PostMapping(value = "/delOneWorkProcess")
    @ResponseBody
    public RestResult delOneWorkProcess(@RequestBody OneWorkProcessData oneWorkProcessData) {
        logger.info("/workProcess/delOneWorkProcess----开始调用删除一级修作业过程接口...");
        RestResult result = RestResult.success();
        try {
            workProcessService.delOneWorkProcess(oneWorkProcessData);
            result.setMsg("删除成功");
        } catch (Exception ex) {
            logger.error("/workProcess/delOneWorkProcess----调用删除一级修作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "删除失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 修改一级修作业过程
     */
    @ApiOperation(value = "修改一级修作业过程", notes = "修改一级修作业过程")
    @PostMapping(value = "/updateOneWorkProcess")
    @ResponseBody
    public RestResult updateOneWorkProcess(HttpServletRequest req) {
        logger.info("/workProcess/updateOneWorkProcess----开始调用修改一级修作业过程接口...");
        RestResult result = RestResult.success();
        try {
            //1.获取业务数据
            String oneWorkData = req.getParameter("oneWorkData");
            JSONObject jsonObject = JSONObject.parseObject(oneWorkData);
            OneWorkProcessData oneWorkProcessData = JSONObject.toJavaObject(jsonObject, OneWorkProcessData.class);
            //2.删除原来的
            workProcessService.delOneWorkProcess(oneWorkProcessData);
            //3.添加新的
            //3.1保存图片到服务器
            List<UploadedFileInfo> uploadedFileInfos = UpLoadFileUtils.uploadImages(req, "workProcess");
            //3.2插入业务数据
            workProcessService.addOneWorkProcess(oneWorkProcessData, uploadedFileInfos);
            result.setMsg("修改成功");
        } catch (Exception ex) {
            logger.error("/workProcess/updateOneWorkProcess----调用修改一级修作业过程口出错...", ex);
            result = RestResult.fromException(ex, logger, "修改失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取总表详细信息集合
     */
    @ApiOperation(value = "获取总表详细信息集合", notes = "获取总表详细信息集合")
    @GetMapping(value = "/getRfidCardSummaryList")
    @ResponseBody
    public RestResult getRfidCardSummaryList(@RequestParam("dayPlanId") String dayPlanId, @RequestParam("trainsetId") String trainsetId, @RequestParam("workerId") String workerId,@RequestParam("itemCode") String itemCode) {
        logger.info("/workProcess/getRfidCardSummaryList----开始调用获取总表详细信息集合接口...");
        RestResult result = RestResult.success();
        try {
            List<ColumnParam<RfidCardSummary>> columnParamList = ColumnParamUtil.filterBlankParamList(
                eqParam(RfidCardSummary::getDayPlanId, dayPlanId),
                eqParam(RfidCardSummary::getFlag, "1"),
                eqParam(RfidCardSummary::getTrainsetId, trainsetId),
                eqParam(RfidCardSummary::getStuffId, workerId),
                eqParam(RfidCardSummary::getRepairType, "0"),
                eqParam(RfidCardSummary::getItemCode,itemCode)
            );
            List<OrderBy<RfidCardSummary>> orderByList = Arrays.asList(
                MybatisPlusUtils.orderBy(RfidCardSummary::getRepairTime, true)
            );
            List<RfidCardSummary> rfidCardSummaryList = MybatisPlusUtils.selectList(
                rfidCardSummaryService,
                columnParamList,
                orderByList
            );
            JSONObject resObj = new JSONObject();
            resObj.put("queryOneWorkProcessDataList", rfidCardSummaryList);
            resObj.put("count", 0);
            result.setData(resObj);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getRfidCardSummaryList----调用获取总表详细信息集合出错...", ex);
            result = RestResult.fromException(ex, logger, "添加");
        }
        return result;
    }

    /**----------------------------------------------------二级修-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 查询二级修列表
     */
    @ApiOperation(value = "查询二级修列表", notes = "查询二级修列表")
    @PostMapping(value = "/getTwoWorkProcessList")
    @ResponseBody
    public RestResult getTwoWorkProcessList(@RequestBody TwoWorkProcessData twoWorkProcessData) {
        logger.info("/workProcess/getTwoWorkProcessList----开始调用查询二级修列表接口...");
        RestResult result = RestResult.success();
        try {
            Page<QueryTwoWorkProcessData> twoWorkProcessList = workProcessService.getTwoWorkProcessList(twoWorkProcessData);
            JSONObject resObj = new JSONObject();
            resObj.put("twoWorkProcessList", twoWorkProcessList);
            result.setData(resObj);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getTwoWorkProcessList----调用查询一级修列表出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/workProcess/getTwoWorkProcessList----调用查询二级修列表接口结束...");
        return result;
    }


    /**
     * @author:
     * @Date:
     * @Description: 查询二级修列表
     */
    @ApiOperation(value = "查询二级修列表", notes = "查询二级修列表")
    @PostMapping(value = "/getTwoWorkProcessData")
    @ResponseBody
    public RestResult getTwoWorkProcessData(@RequestBody TwoWorkProcess twoWorkProcess) {
        logger.info("/workProcess/getTwoWorkProcessData----开始调用查询二级修列表接口...");
        RestResult result = RestResult.success();
        try {
            Page<QueryTwoWorkProcess> twoWorkProcessData = workProcessService.getTwoWorkProcessData(twoWorkProcess);
            JSONObject resObj = new JSONObject();
            resObj.put("twoWorkProcessData", twoWorkProcessData);
            result.setData(resObj);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getTwoWorkProcessData----调用查询二级修列表出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/workProcess/getTwoWorkProcessData----调用查询二级修列表接口结束...");
        return result;
    }




    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 添加二级修作业过程
     */
    @ApiOperation(value = "添加二级修作业过程", notes = "添加二级修作业过程")
    @RequestMapping(value = "/addTwoWorkProcess")
    @ResponseBody
    public RestResult addTwoWorkProcess(HttpServletRequest req) {
        logger.info("/workProcess/addTwoWorkProcess----开始调用添加二级修作业过程接口...");
        RestResult result = RestResult.success();
        try {
            //获取业务数据
            String oneWorkData = req.getParameter("twoWorkData");
            JSONObject jsonObject = JSONObject.parseObject(oneWorkData);
            TwoWorkProcessData twoWorkProcessData = JSONObject.toJavaObject(jsonObject, TwoWorkProcessData.class);
            //保存图片到服务器
            List<UploadedFileInfo> uploadedFileInfos = UpLoadFileUtils.uploadImages(req, "workProcess");
            //插入业务数据
            workProcessService.addTwoWorkProcess(twoWorkProcessData, uploadedFileInfos);
            result.setMsg("添加成功");
        } catch (Exception ex) {
            logger.error("/workProcess/addTwoWorkProcess----调用添加二级修作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "添加失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 删除二级修作业过程
     */
    @ApiOperation(value = "删除二级修作业过程", notes = "删除二级修作业过程")
    @RequestMapping(value = "/delTwoWorkProcess")
    @ResponseBody
    public RestResult delTwoWorkProcess(@RequestBody TwoWorkProcessData twoWorkProcessData) {
        logger.info("/workProcess/delTwoWorkProcess----开始调用删除二级修作业过程接口...");
        RestResult result = RestResult.success();
        try {
            workProcessService.delTwoWorkProcess(twoWorkProcessData);
            result.setMsg("删除成功");
        } catch (Exception ex) {
            logger.error("/workProcess/delTwoWorkProcess----调用删除二级修作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "删除失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 修改二级修作业过程
     */
    @ApiOperation(value = "修改二级修作业过程", notes = "修改二级修作业过程")
    @PostMapping(value = "/updateTwoWorkProcess")
    @ResponseBody
    public RestResult updateTwoWorkProcess(HttpServletRequest req) {
        logger.info("/workProcess/updateOneWorkProcess----开始调用修改二级修作业过程接口...");
        RestResult result = RestResult.success();
        try {
            //获取业务数据
            String oneWorkData = req.getParameter("twoWorkData");
            JSONObject jsonObject = JSONObject.parseObject(oneWorkData);
            TwoWorkProcessData twoWorkProcessData = JSONObject.toJavaObject(jsonObject, TwoWorkProcessData.class);
            //1.删除原来的
            workProcessService.delTwoWorkProcess(twoWorkProcessData);
            //2.添加新的
            //保存图片到服务器
            List<UploadedFileInfo> uploadedFileInfos = UpLoadFileUtils.uploadImages(req, "workProcess");
            //插入业务数据
            workProcessService.addTwoWorkProcess(twoWorkProcessData, uploadedFileInfos);
            result.setMsg("修改成功");
        } catch (Exception ex) {
            logger.error("/workProcess/updateOneWorkProcess----调用修改二级修作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "修改失败");
        }
        return result;
    }

    /**-----------------------------------------------一体化-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取一体化查询列表
     */
    @ApiOperation(value = "获取一体化查询列表", notes = "获取一体化查询列表")
    @RequestMapping(value = "/getIntegrationList")
    @ResponseBody
    public RestResult getIntegrationList(@RequestBody ProcessData processData) {
        logger.info("/workProcess/getIntegrationList----开始调用获取一体化查询列表接口...");
        RestResult result = RestResult.success();
        try {
            List<IntegrationProcessData> integrationList = workProcessService.getIntegrationList(processData);
            JSONObject resObj = new JSONObject();
            resObj.put("integrationList", integrationList);
            resObj.put("count", integrationList.size());
            result.setData(resObj);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getIntegrationList----调用获取一体化查询列表出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 无修程作业过程确认
     */
    @ApiOperation(value = "无修程作业过程确认", notes = "无修程作业过程确认")
    @RequestMapping(value = "/addIntegration")
    @ResponseBody
    public RestResult addIntegration(@RequestBody IntegrationProcessData integrationProcessData) {
        logger.info("/workProcess/addIntegration----开始调用无修程作业过程确认接口...");
        RestResult result = RestResult.success();
        try {
            workProcessService.addIntegration(integrationProcessData,"2");
            result.setMsg("添加成功");
        } catch (Exception ex) {
            logger.error("/workProcess/addIntegration----调用无修程作业过程确认出错...", ex);
            result = RestResult.fromException(ex, logger, "添加失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 删除无修程作业过程
     */
    @ApiOperation(value = "删除无修程作业过程", notes = "删除无修程作业过程")
    @PostMapping(value = "/delIntegration")
    @ResponseBody
    public RestResult delIntegration(@RequestBody IntegrationProcessData integrationProcessData) {
        logger.info("/workProcess/delIntegration----开始调用删除无修程作业过程接口...");
        RestResult result = RestResult.success();
        try {
            workProcessService.delIntegration(integrationProcessData);
            result.setMsg("删除成功");
        } catch (Exception ex) {
            logger.error("/workProcess/delIntegration----调用删除无修程作业过程出错...", ex);
            result = RestResult.fromException(ex, logger, "删除失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 修改无修程作业过程
     */
    @ApiOperation(value = "修改无修程作业过程", notes = "修改无修程作业过程")
    @PostMapping(value = "/updateIntegration")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult updateIntegration(@RequestBody IntegrationProcessData integrationProcessData) {
        logger.info("/workProcess/updateIntegration----开始调用修改无修程作业过程接口...");
        RestResult result = RestResult.success();
        try {
            //1.删除
            workProcessService.delIntegration(integrationProcessData);
            //2.新增
            workProcessService.addIntegration(integrationProcessData,"2");
            result.setMsg("修改成功");
        } catch (Exception ex) {
            logger.error("/workProcess/updateIntegration----调用修改无修程作业过程出错...", ex);
            result = RestResult.fromException(ex, logger, "修改失败");
        }
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取无修程车组集合
     */
    @ApiOperation(value = "获取无修程车组集合", notes = "获取无修程车组集合")
    @GetMapping(value = "/getIntegrationTrainsetList")
    @ResponseBody
    public RestResult getIntegrationTrainsetList(@RequestParam("unitCode") String unitCode, @RequestParam("dayPlanId") String dayPlanId, @RequestParam("repairType") String repairType) {
        logger.info("/workProcess/getIntegrationTrainsetList----开始调用获取无修程车组集合接口...");
        RestResult result = RestResult.success();
        try {
            if (dayPlanId == null || dayPlanId.equals("")) {
                throw new RuntimeException("日计划不能为空");
            }
            if (repairType == null || repairType.equals("")) {
                throw new RuntimeException("作业类型不能为空");
            }
            if (unitCode == null || unitCode.equals("")) {
                ShiroUser currentUser = ShiroKit.getUser();
                unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
            }
            List<Map<String, String>> resList = workProcessService.getIntegrationTrainsetList(unitCode,dayPlanId,repairType);
            result.setData(resList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getIntegrationTrainsetList----调用获取无修程车组集合出错...", ex);
            result = RestResult.fromException(ex, logger, "添加");
        }
        logger.info("/workProcess/getIntegrationTrainsetList----调用获取无修程车组集合接口结束...");
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 无修程作业过程作业任务集合
     */
    @ApiOperation(value = "无修程作业过程作业任务集合", notes = "无修程作业过程作业任务集合")
    @GetMapping(value = "/getIntegrationTaskList")
    @ResponseBody
    public RestResult getIntegrationTaskList(@RequestParam("dayPlanId") String dayPlanId,@RequestParam("trainsetId") String trainsetId,@RequestParam("repairDeptCode") String repairDeptCode,@RequestParam("deptCode") String unitCode,@RequestParam("lstPacketTypeCode") String lstPacketTypeCode) {
        logger.info("/workProcess/getIntegrationTaskList----开始调用获取车组集合接口...");
        RestResult result = RestResult.success();
        try {
            if (dayPlanId == null || dayPlanId.equals("")) {
                throw new RuntimeException("日计划不能为空");
            }
            if (lstPacketTypeCode == null || lstPacketTypeCode.equals("")) {
                throw new RuntimeException("作业类型不能为空");
            }
            if (unitCode == null || unitCode.equals("")) {
                ShiroUser currentUser = ShiroKit.getUser();
                unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
            }
            List<ZtTaskPacketEntity> resList = workProcessService.getIntegrationTaskList(dayPlanId,trainsetId,repairDeptCode,unitCode,lstPacketTypeCode);
            result.setData(resList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getIntegrationTaskList----调用获取无修程作业过程作业任务集合出错...", ex);
            result = RestResult.fromException(ex, logger, "调用获取无修程作业过程作业任务集合出错");
        }
        logger.info("/workProcess/getIntegrationTaskList----调用获取无修程作业过程作业任务集合接口结束...");
        return result;
    }



    /**-----------------------------------------------------公用-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取车组集合
     */
    @ApiOperation(value = "获取车组集合", notes = "获取车组集合")
    @GetMapping(value = "/getTrainsetListByDayPlanId")
    @ResponseBody
    public RestResult getTrainsetListByDayPlanId(@RequestParam("unitCode") String unitCode, @RequestParam("dayPlanId") String dayPlanId, @RequestParam("repairType") String repairType) {
        logger.info("/workProcess/getTrainsetIdListByDayPlanId----开始调用获取车组集合接口...");
        RestResult result = RestResult.success();
        try {
            if (dayPlanId == null || dayPlanId.equals("")) {
                throw new RuntimeException("日计划不能为空");
            }
            if (repairType == null || repairType.equals("")) {
                throw new RuntimeException("作业类型不能为空");
            }
            if (unitCode == null || unitCode.equals("")) {
                ShiroUser currentUser = ShiroKit.getUser();
                unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
            }
            //2.取检修任务中台获取任务
            List<String> packetTypeCodeList = new ArrayList<>();
            packetTypeCodeList.add("1");
            packetTypeCodeList.add("6");
            String packetTypeStrs = String.join(",", packetTypeCodeList);
            List<ZtTaskPacketEntity> ztTaskPacketEntityList = iRemoteService.getPacketTaskByCondition(dayPlanId, "", packetTypeStrs, "", unitCode).stream().collect(Collectors.toList());
            if (repairType.equals("1")) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getTaskRepairCode().equals("1")).collect(Collectors.toList());
            } else if (repairType.equals("2")) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getTaskRepairCode().equals("2")).collect(Collectors.toList());
            } else if (repairType.equals("6")) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("6")).collect(Collectors.toList());
            }
            //3.将数据组织为车组集合
            List<Map<String, String>> resList = new ArrayList<>();
            resList = ztTaskPacketEntityList.stream().map(entity -> {
                Map<String, String> item = new HashMap<>();
                item.put("trainsetId", entity.getTrainsetId());
                item.put("trainsetName", entity.getTrainsetName());
                return item;
            }).distinct().collect(Collectors.toList());
            result.setData(resList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getTrainsetIdListByDayPlanId----调用获取车组集合出错...", ex);
            result = RestResult.fromException(ex, logger, "添加");
        }
        logger.info("/workProcess/getTrainsetIdListByDayPlanId----调用获取车组集合接口结束...");
        return result;
    }


    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取作业班组集合
     */
    @ApiOperation(value = "获取作业班组集合", notes = "获取作业班组集合")
    @GetMapping(value = "/getTaskDeptList")
    @ResponseBody
    public RestResult getTaskDeptList(@RequestParam("unitCode") String unitCode, @RequestParam("dayPlanId") String dayPlanId, @RequestParam("repairType") String repairType, @RequestParam("trainsetId") String trainsetId, @RequestParam("packetCode") String packetCode) {
        logger.info("/workProcess/getTaskDeptList----开始调用获取获取作业班组集合接口...");
        RestResult result = RestResult.success();
        try {
            if (dayPlanId == null || dayPlanId.equals("")) {
                throw new RuntimeException("日计划不能为空");
            }
            if (repairType == null || repairType.equals("")) {
                throw new RuntimeException("作业类型不能为空");
            }
            if (trainsetId == null || trainsetId.equals("")) {
                throw new RuntimeException("车组不能为空");
            }
            if (unitCode == null || unitCode.equals("")) {
                ShiroUser currentUser = ShiroKit.getUser();
                unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
            }
            List<String> packetTypeCodeList = new ArrayList<>();
            packetTypeCodeList.add("6");
            packetTypeCodeList.add("5");
            packetTypeCodeList.add("1");
            String packetTypeStrs = String.join(",", packetTypeCodeList);
            //2.去检修任务中台获取任务
            List<ZtTaskPacketEntity> ztTaskPacketEntityList = iRemoteService.getPacketTaskByCondition(dayPlanId, trainsetId, packetTypeStrs, "", unitCode).stream().collect(Collectors.toList());
            if (repairType.equals("1")) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("1") && t.getTaskRepairCode().equals("1")).collect(Collectors.toList());
            } else if (repairType.equals("2")) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("1") && t.getTaskRepairCode().equals("2")).collect(Collectors.toList());
            } else if (repairType.equals("6")) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("6")).collect(Collectors.toList());
            }
            if (packetCode != null && !packetCode.equals("")) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
            }
            List<Map<String, String>> resList = new ArrayList<>();
            resList = ztTaskPacketEntityList.stream().map(entity -> {
                Map<String, String> item = new HashMap<>();
                item.put("deptCode", entity.getRepairDeptCode());
                item.put("deptName", entity.getRepairDeptName());
                return item;
            }).distinct().collect(Collectors.toList());
            result.setData(resList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getTaskDeptList----调用获取作业班组集合出错...", ex);
            result = RestResult.fromException(ex, logger, "添加");
        }
        logger.info("/workProcess/getTaskDeptList----调用获获取作业班组集合接口结束...");
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 根据班组获取班组下所有人员
     */
    @ApiOperation(value = "根据班组获取班组下所有人员", notes = "根据班组获取班组下所有人员")
    @GetMapping(value = "/getPersonByDept")
    @ResponseBody
    public RestResult getPersonByDept(@RequestParam("deptCode") String deptCode) {
        logger.info("/workProcess/getPersonByDept----开始调用根据班组获取班组下所有人员接口...");
        RestResult result = RestResult.success();
        try {
            List<Map<String, String>> resList = new ArrayList<>();
            if (!ObjectUtils.isEmpty(deptCode)) {
                List<SysStaff> workerList = UserUtil.getStaffListStruByDept(deptCode);
                if (workerList != null && workerList.size() > 0) {
                    workerList = workerList.stream().filter(t -> (!StringUtils.isBlank(t.getStaffId())) || (!StringUtils.isBlank(t.getName()))).collect(Collectors.toList());
                    workerList = CommonUtils.getDistinctList(workerList, t -> t.getStaffId());
                }
                resList = workerList.stream().map(entity -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("workId", entity.getStaffId());
                    item.put("workName", entity.getName());
                    return item;
                }).collect(Collectors.toList());
            }
            result.setData(resList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcess/getPersonByDept----调用根据班组获取班组下所有人员出错...", ex);
            result = RestResult.fromException(ex, logger, "添加");
        }
        logger.info("/workProcess/getPersonByDept----调用根据班组获取班组下所有人员结束...");
        return result;
    }
}
