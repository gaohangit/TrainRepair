package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.controller;

import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidPosition;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.XzyCRfidRegist;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarCritertion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.IXzyBRfidPlaceTypeService;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.IXzyCRfidRegistService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateCurService;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * 标签注册(RFID)控制器
 *
 * @author 于雨新
 * @Date 2020-04-29 14:40:45
 * @modified_by 张天可 2021年1月7日16:30:46
 * @modified_by 冯帅 2021年4月22日
 */
@Api("标签注册")
@Controller
@RequestMapping(RfidRegistController.BASE_MAPPING)
public class RfidRegistController {
    static final String BASE_MAPPING = "/rfidRegist";

    @Autowired
    IXzyCRfidRegistService xzyCRfidregistService;

    @Autowired
    IXzyBRfidPlaceTypeService xzyBRfidplaceTypeService;

    @Autowired
    IRemoteService remoteService;

    @Autowired
    TrackPowerStateCurService trackPowerStateCurService;

    protected static final Logger logger = LoggerFactory.getLogger(RfidRegistController.class);

    /**
     * 获取标签注册(RFID)列表
     *
     * @return 返回 标签注册(RFID)列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "标签注册-获取标签注册(RFID)列表", notes = "标签注册-获取标签注册(RFID)列表")
    @ResponseBody
    @BussinessLog(value = "标签注册-获取标签注册(RFID)列表", key = "/rfidRegist/list", type = "04")
    public RestResult list(XzyCRfidRegist model,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        try {
            //1 rfid标签列表 2标签位置列表 3标签关系列表
            return xzyCRfidregistService.selectRfIdList(model, pageNum, pageSize);
        } catch (Exception ex) {
            logger.error("rfidRegist/list------调用标签注册获取列表接口出错...", ex);
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 新增标签注册(RFID)
     *
     * @return 成功或者失败
     */
    @ApiOperation(value = "标签注册-新增", notes = "标签注册-新增")
    @PostMapping(value = "/add")
    @ResponseBody
    @BussinessLog(value = "标签注册-新增", key = "/rfidRegist/add", type = "01")
    public synchronized RestResult add(@RequestBody XzyCRfidRegist model) {
        try {
            logger.info("rfidRegist/add------开始调用标签注册新增接口...");
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            if (StringUtils.isEmpty(model.getsTrackcode()) || StringUtils.isEmpty(model.getsRepairplacecode()) || StringUtils.isEmpty(model.getsTid())) {
                throw RestRequestException.normalFail("参数错误");
            }
            List<ColumnParam<XzyCRfidRegist>> queryColumnParams = Arrays.asList(
                eqParam(XzyCRfidRegist::getsTrackcode, model.getsTrackcode()),
                eqParam(XzyCRfidRegist::getsRepairplacecode, model.getsRepairplacecode()),
                eqParam(XzyCRfidRegist::getsPillarname, model.getsPillarname()),
                eqParam(XzyCRfidRegist::getcFlag, "1")
            );

            if (MybatisPlusUtils.selectCount(xzyCRfidregistService, queryColumnParams) > 0) {
                throw RestRequestException.normalFail("位置已注册，无法重复注册！");
            }
            if (MybatisPlusUtils.selectCount(
                xzyCRfidregistService,
                eqParam(XzyCRfidRegist::getsTid, model.getsTid()),
                eqParam(XzyCRfidRegist::getcFlag, "1")
            ) > 0) {
                throw RestRequestException.normalFail("标签已注册，无法重复注册！");
            }
            model.setcFlag("1");
            model.setdCreatetime(new Date());
            model.setsCreateUserCode(currentUser.getStaffId());
            model.setsCreateUserName(currentUser.getName());
            xzyCRfidregistService.insert(model);
            logger.info("rfidRegist/add------调用标签注册新增接口结束...");
            return RestResult.success().setData(MybatisPlusUtils.selectList(xzyCRfidregistService, queryColumnParams).get(0));
        } catch (Exception ex) {
            logger.error("rfidRegist/add------调用标签注册新增接口出错...", ex);
            return RestResult.fromException(ex, logger, null);
        }
    }

    @Data
    public static class RfidRegistKeysWarp extends Object {
        List<String> rfidRegistIds;
    }

    /**
     * 删除标签注册(RFID)--假删除
     *
     * @param rfidRegistKeysWarp id列表
     * @return 成功或者失败
     */
    @ApiOperation(value = "标签注册(RFID)-删除", notes = "标签注册(RFID)-删除")
    @PostMapping(value = "/delRfid")
    @ResponseBody
    @BussinessLog(value = "标签注册(RFID)-删除", key = "/rfidRegist/delRfid", type = "02")
    public RestResult delete(@RequestBody RfidRegistKeysWarp rfidRegistKeysWarp) {
        try {
            logger.info("rfidRegist/delRfid------开始调用标签删除接口...");
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            xzyCRfidregistService.delRfid(rfidRegistKeysWarp.getRfidRegistIds(), currentUser.getStaffId(), currentUser.getName());
//            xzyCRfidregistService.deleteBatchIds(rfidRegistKeysWarp.getRfidRegistIds());
            logger.info("rfidRegist/delRfid------调用标签删除接口结束...");
            return RestResult.success();
        } catch (Exception ex) {
            logger.error("rfidRegist/delRfid------调用标签删除接口出错...", ex);
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 修改标签注册(RFID)
     *
     * @param model 标签注册(RFID) 实体
     * @return 成功或者失败
     */
    @ApiOperation(value = "标签注册(RFID)-修改", notes = "标签注册(RFID)-修改")
    @PostMapping(value = "/update")
    @ResponseBody
    @BussinessLog(value = "标签注册(RFID)-修改", key = "/rfidRegist/update", type = "03", pk = "sId")
    public RestResult update(XzyCRfidRegist model) {
        try {
            if (MybatisPlusUtils.selectCount(
                xzyCRfidregistService,
                eqParam(XzyCRfidRegist::getsId, model.getsId())
            ) == 0) {
                throw RestRequestException.normalFail("无该数据，修改失败！");
            }
            xzyCRfidregistService.updateById(model);
            return RestResult.success().setData(
                xzyCRfidregistService.selectById(model.getsId())
            );
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 获取标签注册位置字典列表
     *
     * @return 返回标签注册位置字典列表
     */
    @ApiOperation(value = "RFID标签配置-获取作业位置", notes = "RFID标签配置-获取作业位置")
    @GetMapping(value = "/getPlaceTypes")
    @ResponseBody
    public RestResult getPlaceTypes() {
        try {
            return RestResult.success().setData(MybatisPlusUtils.selectAll(xzyBRfidplaceTypeService));
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 获取股道基本信息
     *
     * @author 张天可
     */
    @RequestMapping("/getTrackAreas")
    @ResponseBody
    @ApiOperation(value = "获取股道基本信息", notes = "获取股道基本信息")
    @BussinessLog(value = "获取股道基本信息", key = BASE_MAPPING + "/getTrackAreas", type = "04")
    public Object getTrackAreas(@RequestParam String deptCode) {
        try {
            List<ZtTrackAreaEntity> trackAreas = trackPowerStateCurService.getTrackAreaByDept(deptCode);
            return RestResult.success().setData(trackAreas);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, null);
        }
    }

    /**
     * @author: 吴跃常
     * @Description: 新增标签作业位置信息
     * @date: 2021-07-28 15:12
     */
    @PostMapping("/addPosition")
    @ResponseBody
    @ApiOperation(value = "新增标签作业位置信息", notes = "新增标签作业位置信息")
    @BussinessLog(value = "新增标签作业位置信息", key = BASE_MAPPING + "/addPostion", type = "04")
    public Object addPosition(@RequestBody RfidPosition rfidPosition) {
        try {
            ShiroUser currentUser = ShiroKit.getUser();
            xzyCRfidregistService.addPosition(rfidPosition, currentUser);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, null);
        }
    }

    /**
     * @author: 吴跃常
     * @Description: 新增标签标准关系信息
     * @date: 2021-07-28 15:12
     */
    @PostMapping("/addCritertion")
    @ResponseBody
    @ApiOperation(value = "新增标签标准关系信息", notes = "新增标签标准关系信息")
    @BussinessLog(value = "新增标签标准关系信息", key = BASE_MAPPING + "/addCritertion", type = "04")
    public Object addCritertion(@RequestBody XzyCRfidcarCritertion cRfidcarCritertion) {
        try {
            ShiroUser currentUser = ShiroKit.getUser();
            cRfidcarCritertion.setId(UUID.randomUUID().toString());
            cRfidcarCritertion.setCreateTime(new Date());
            cRfidcarCritertion.setCreateUserCode(currentUser.getStaffId());
            cRfidcarCritertion.setCreateUserName(currentUser.getName());
            xzyCRfidregistService.addCritertion(cRfidcarCritertion);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, null);
        }
    }

    /**
     * 获取rfid位置信息
     *
     * @return 返回 获取rfid位置列表
     */
    @GetMapping(value = "/positionList")
    @ApiOperation(value = "获取rfid位置信息", notes = "获取rfid位置信息")
    @ResponseBody
    @BussinessLog(value = "获取rfid位置信息", key = "/rfidRegist/positionList", type = "04")
    public RestResult getRfIdPosition(@RequestParam Integer pageNum,
                                      @RequestParam Integer pageSize,
                                      @RequestParam(required = false) String tid,
                                      @RequestParam(required = false) String trackCode,
                                      @RequestParam(required = false) String placeCode,
                                      @RequestParam(required = false) Integer carCount) {
        try {
            String unitCode = ContextUtils.getUnitCode();
            return xzyCRfidregistService.selectRfIdPosition(pageNum, pageSize, tid, trackCode, placeCode, carCount, unitCode);
        } catch (Exception ex) {
            logger.error("rfidRegist/list------获取rfid位置信息接口出错...", ex);
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 获取rfid标准关系信息
     *
     * @return 返回 获取rfid标准关系信息
     */
    @GetMapping(value = "/critertionList")
    @ApiOperation(value = "获取rfid标准关系信息", notes = "获取rfid标准关系信息")
    @ResponseBody
    @BussinessLog(value = "获取rfid标准关系信息", key = "/rfidRegist/critertionList", type = "04")
    public RestResult getRfIdCritertion(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize,
                                        @RequestParam(required = false) String itemName,
                                        @RequestParam(required = false) String trainsetType,
                                        @RequestParam(required = false) String trainsetSubType,
                                        @RequestParam(required = false) String repairPlaceCode) {
        try {
            return xzyCRfidregistService.selectRfIdCriterion(pageNum, pageSize, itemName, trainsetSubType, trainsetType, repairPlaceCode);
        } catch (Exception ex) {
            logger.error("rfidRegist/list------获取rfid标准关系信息接口出错...", ex);
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 删除标签位置
     */
    @PostMapping(value = "/delPosition")
    @ApiOperation(value = "删除标签位置", notes = "删除标签位置")
    @ResponseBody
    @BussinessLog(value = "删除标签位置", key = "/rfidRegist/positionList", type = "04")
    public RestResult delPosition(@RequestBody List<String> ids) {
        try {
            xzyCRfidregistService.delPosition(ids);
            return RestResult.success();
        } catch (Exception ex) {
            logger.error("rfidRegist/list------删除标签位置接口出错...", ex);
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 删除标签标准关系
     */
    @PostMapping(value = "/delCritertion")
    @ApiOperation(value = "删除标签标准关系", notes = "删除标签标准关系")
    @ResponseBody
    @BussinessLog(value = "删除标签标准关系", key = "/rfidRegist/delCritertion", type = "04")
    public RestResult delCritertion(@RequestBody List<String> ids) {
        try {
            xzyCRfidregistService.delCritertion(ids);
            return RestResult.success();
        } catch (Exception ex) {
            logger.error("rfidRegist/list------删除标签位置接口出错...", ex);
            return RestResult.fromException(ex, logger, null);
        }
    }
}
