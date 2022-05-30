package com.ydzbinfo.emis.trainRepair.warnmanagent.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.warnmanagent.model.WorkWarningQueryParam;
import com.ydzbinfo.emis.trainRepair.warnmanagent.querymodel.WorkWorning;
import com.ydzbinfo.emis.trainRepair.warnmanagent.service.IWorkWorningService;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.OrderBy;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * <p>
 * 作业过程预警
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-07
 */
@Controller
@RequestMapping("/warnmanagent")
public class WorkWorningController {
    //日志记录
    protected static final Logger logger = LoggerFactory.getLogger(WorkWorningController.class);

    @Autowired
    IWorkWorningService workWorningService;

    @ApiOperation(value = "获取预警数据列表", notes = "获取预警数据列表")
    @PostMapping(value = "/getWorkWorningList")
    @ResponseBody
    public RestResult getWorkWorningList(@RequestBody WorkWarningQueryParam model) {
        logger.info("/warnmanagent/getWorkWorningList----开始调用获取预警数据列表接口...");
        RestResult result = RestResult.success();
        try {
            List<ColumnParam<WorkWorning>> columnParamList = ColumnParamUtil.filterBlankParamList(
                eqParam(WorkWorning::getTrainsetType, model.getTrainsetType()),
                eqParam(WorkWorning::getTrainsetName, model.getTrainsetName()),
                eqParam(WorkWorning::getEffectState, model.getEffectState()),
                geParam(WorkWorning::getCreateTime, model.getStartTime()),
                leParam(WorkWorning::getCreateTime, model.getEndTime())
            );
            List<OrderBy<WorkWorning>> orderByList = Arrays.asList(
                MybatisPlusUtils.orderBy(WorkWorning::getEffectState, false),
                MybatisPlusUtils.orderBy(WorkWorning::getCreateTime, false)
            );
            Page<WorkWorning> workWorningPage = MybatisPlusUtils.selectPage(
                workWorningService,
                model.getPageNum(),
                model.getPageSize(),
                columnParamList,
                orderByList
            );
            result.setData(workWorningPage);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/warnmanagent/getWorkWorningList----调用获取预警数据列表接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/warnmanagent/getWorkWorningList----调用获取预警数据列表接口结束");
        return result;
    }

    @ApiOperation(value = "作业过程风险预警确认", notes = "作业过程风险预警确认")
    @PostMapping(value = "/effectWorkWorning")
    @ResponseBody
    public RestResult effectWorkWorning(@RequestBody WorkWorning workWorning) {
        logger.info("/warnmanagent/effectWorkWorning----开始调用作业过程风险预警确认接口...");
        RestResult result = RestResult.success();
        try {
            workWorning.setEffectState("0");
            boolean b = workWorningService.updateById(workWorning);
            result.setMsg("确认成功");
        } catch (Exception ex) {
            logger.error("/warnmanagent/effectWorkWorning----调用作业过程风险预警确认接口出错...", ex);
            result = RestResult.fromException(ex, logger, "确认失败");
        }
        logger.info("/warnmanagent/effectWorkWorning----调用作业过程风险预警确认接口结束");
        return result;
    }

}
