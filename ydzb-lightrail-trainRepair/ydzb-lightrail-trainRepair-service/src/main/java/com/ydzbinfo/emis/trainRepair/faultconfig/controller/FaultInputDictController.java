package com.ydzbinfo.emis.trainRepair.faultconfig.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.faultconfig.model.FaultInputDictQueryParam;
import com.ydzbinfo.emis.trainRepair.faultconfig.querymodel.FaultInputDict;
import com.ydzbinfo.emis.trainRepair.faultconfig.service.IFaultInputDictService;
import com.ydzbinfo.emis.trainRepair.faultconfig.util.FaultUtil;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.OrderBy;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.orderBy;


/**
 * <p>
 * 故障配置
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-07
 */
@Controller
@RequestMapping("/faultconfig")
public class FaultInputDictController {

    //日志记录
    protected static final Logger logger = LoggerFactory.getLogger(FaultInputDictController.class);

    @Autowired
    IFaultInputDictService faultInputDictService;


    @ApiOperation(value = "获取故障配置列表", notes = "获取故障配置列表")
    @PostMapping(value = "/getFaultInputDictList")
    @ResponseBody
    public RestResult getFaultInputDictList(@RequestBody FaultInputDictQueryParam model) {
        logger.info("/faultconfig/getFaultInputDictList----开始调用获取故障配置列表接口...");
        RestResult result = RestResult.success();
        try {
            model.setFlag("1");
            List<ColumnParam<FaultInputDict>> columnParamList = FaultUtil.toColumnParamList(model);
            List<OrderBy<FaultInputDict>> orderByList = Collections.singletonList(
                orderBy(FaultInputDict::getRecordTime, false)
            );
            Page<FaultInputDict> faultInputDictPage = MybatisPlusUtils.selectPage(
                faultInputDictService,
                model.getPageNum(),
                model.getPageSize(),
                columnParamList,
                orderByList
            );
            result.setData(faultInputDictPage);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/faultconfig/getFaultInputDictList----调用获取故障配置列表接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/faultconfig/getFaultInputDictList----调用获取故障配置列表接口结束");
        return result;
    }

    @ApiOperation(value = "新增故障配置", notes = "新增故障配置")
    @PostMapping(value = "/addFaultInputDict")
    @ResponseBody
    public RestResult addFaultInputDict(@RequestBody FaultInputDict faultInputDict) {
        logger.info("/faultconfig/addFaultInputDict----开始调用新增故障配置接口...");
        RestResult result = RestResult.success();
        try {
            //查询该关键字在数据库中是否有数据
            int count = MybatisPlusUtils.selectCount(
                faultInputDictService,
                eqParam(FaultInputDict::getKey, faultInputDict.getKey()),
                eqParam(FaultInputDict::getFlag, "1")
            );
            if (count > 0) {
                throw new RuntimeException("该关键字已存在!");
            } else {
                //获取当前用户
                ShiroUser currentUser = ShiroKit.getUser();
                faultInputDict.setRecorderCode(currentUser.getStaffId());
                faultInputDict.setRecorderName(currentUser.getName());
                faultInputDict.setRecordTime(new Date());
                faultInputDict.setFlag("1");
                boolean insertFlag = faultInputDictService.insert(faultInputDict);
                result.setMsg("新增成功");
            }
        } catch (Exception ex) {
            logger.error("/faultconfig/addFaultInputDict----调用新增故障配置接口出错...", ex);
            result = RestResult.fromException(ex, logger, "新增失败");
        }
        logger.info("/faultconfig/addFaultInputDict----调用新增故障配置接口结束");
        return result;
    }

    @ApiOperation(value = "删除故障配置", notes = "删除故障配置")
    @GetMapping(value = "/delFaultInputDict")
    @ResponseBody
    public RestResult delFaultInputDict(@RequestParam("faultInputId") String faultInputId) {
        logger.info("/faultconfig/delFaultInputDict----开始调用删除故障配置接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            FaultInputDict delFault = new FaultInputDict();
            delFault.setFaultInputId(faultInputId);
            delFault.setFlag("0");
            delFault.setDelTime(new Date());
            delFault.setDelUserCode(currentUser.getStaffId());
            delFault.setDelUserName(currentUser.getName());
            boolean delFalt = faultInputDictService.updateById(delFault);
            result.setMsg("删除成功");
        } catch (Exception ex) {
            logger.error("/faultconfig/delFaultInputDict----调用删除故障配置接口出错...", ex);
            result = RestResult.fromException(ex, logger, "删除失败");
        }
        logger.info("/faultconfig/delFaultInputDict----调用删除故障配置接口结束");
        return result;
    }

    @ApiOperation(value = "修改故障配置", notes = "修改故障配置")
    @PostMapping(value = "/updateFaultInputDict")
    @ResponseBody
    public RestResult updateFaultInputDict(@RequestBody FaultInputDict faultInputDict) {
        logger.info("/faultconfig/updateFaultInputDict----开始调用修改故障配置接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            boolean flag = faultInputDictService.updateById(faultInputDict);
            result.setMsg("修改成功");
        } catch (Exception ex) {
            logger.error("/faultconfig/delFaultInputDict----调用修改故障配置接口出错...", ex);
            result = RestResult.fromException(ex, logger, "修改失败");
        }
        logger.info("/faultconfig/delFaultInputDict----调用修改故障配置接口结束");
        return result;
    }


}
