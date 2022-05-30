package com.ydzbinfo.emis.trainRepair.bill.fillback.controller;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.ICheckInfoService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Description: 一二级修记录单回填控制器
 * @Data: 2021/7/31
 * @Author: 冯帅
 */
@RestController
@RequestMapping("/checkInfo")
public class CheckInfoController {

    //日志类
    protected static final Logger logger = LoggerFactory.getLogger(CheckInfoController.class);

    //返回状态码

    @Autowired
    ICheckInfoService checkInfoService;

    /**
     * @author: 韩旭
     * @date: 2021/7/31
     * @param: [ChecklistSummary]
     * @return: com.ydzbinfo.emis.utils.result.RestResult
     */
    @ApiOperation(value = "获取一二级修单据详细数据", notes = "获取一二级修单据详细数据")
    @PostMapping(value = "/getCheckDetail")
    @ResponseBody
    public RestResult getCheckDetail(@RequestBody OneTwoRepairCheckList checklistSummary) {
        try {
            ChecklistSummaryInfoForShow info = checkInfoService.getDetailInfoShow(checklistSummary);
            return RestResult.success().setData(info);
        } catch (Exception ex) {
            logger.error("/checkInfo/getCheckDetail----调用获取一二级修单据详细数据接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }

    @ApiOperation(value = "一二级修单据工长签字", notes = "一二级修单据工长签字")
    @PostMapping(value = "/setOneTwoRepairForemanSign")
    @ResponseBody
    public RestResult setOneTwoRepairForemanSign(@RequestBody ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.setOneTwoRepairForemanSign(saveInfo);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/setOneTwoRepairForemanSign----调用一二级修单据工长签字接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }

    @ApiOperation(value = "一二级修单据质检签字", notes = "一二级修单据质检签字")
    @PostMapping(value = "/setOneTwoRepairQualitySign")
    @ResponseBody
    public RestResult setOneTwoRepairQualitySign(@RequestBody ChecklistSummaryInfoForSave saveInfo) {
        try {
            //进行签字
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.setOneTwoRepairQualitySign(saveInfo);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/setOneTwoRepairQualitySign----调用一二级修单据质检签字数据接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }


    @ApiOperation(value = "一二级修单据批量工长签字", notes = "一二级修单据批量工长签字")
    @PostMapping(value = "/setOneTwoRepairAllForemanSign")
    @ResponseBody
    public RestResult setOneTwoRepairAllForemanSign(@RequestBody List<OneTwoRepairCheckList> summaryList) {
        try {
            SignMessage msg = checkInfoService.setOneTwoRepairAllForemanSign(summaryList);
            if(msg.getCode().equals("1")){
                return RestResult.success().setMsg(msg.getMsg());
            }else if(msg.getCode().equals("0")){
                RestMessage restMessage = new RestMessage();
                restMessage.setWarnMsg(msg.getMsg());
                return RestResult.success().setData(restMessage);
            }else {
                throw RestRequestException.fatalFail(msg.getMsg());
            }
        } catch (Exception ex) {
            logger.error("/checkInfo/setOneTwoRepairAllForemanSign----调用一二级修单据批量工长签字数据接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }

    @ApiOperation(value = "一二级修单据批量质检签字", notes = "一二级修单据批量质检签字")
    @PostMapping(value = "/setOneTwoRepairAllQualitySign")
    @ResponseBody
    public RestResult setOneTwoRepairAllQualitySign(@RequestBody List<OneTwoRepairCheckList> summaryList) {
        try {
            SignMessage msg = checkInfoService.setOneTwoRepairAllQualitySign(summaryList);
            if(msg.getCode().equals("1")){
                return RestResult.success().setMsg(msg.getMsg());
            }else if(msg.getCode().equals("0")){
                RestMessage restMessage = new RestMessage();
                restMessage.setWarnMsg(msg.getMsg());
                return RestResult.success().setData(restMessage);
            }else {
                throw RestRequestException.fatalFail(msg.getMsg());
            }
        } catch (Exception ex) {
            logger.error("/checkInfo/setOneTwoRepairAllQualitySign----调用一二级修单据批量质检签字数据接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }

    @ApiOperation(value = "故障跳转", notes = "故障跳转")
    @PostMapping(value = "/faultJump")
    @ResponseBody
    public RestResult faultJump(@RequestBody ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.faultJump(saveInfo);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/faultJump----故障跳转接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }


    @ApiOperation(value = "记录单跳转故障后的故障录入信息", notes = "记录单跳转故障后的故障录入信息")
    @PostMapping(value = "/faultJumpByChecklist")
    @ResponseBody
    public RestResult faultJumpByChecklist(@RequestBody FaultJumpByChecklist faultJumpByChecklist) {
        try {
            String strError = checkInfoService.faultJumpByChecklist(faultJumpByChecklist);
            return RestResult.success().setMsg(strError);
        } catch (Exception ex) {
            logger.error("/checkInfo/faultJumpByChecklist----记录单跳转故障后的故障录入信息接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }


    @ApiOperation(value = "保存记录单", notes = "保存记录单")
    @PostMapping(value = {"/saveRepairCell", "/SaveRepairCell"})
    @ResponseBody
    public RestResult saveRepairCell(@RequestBody ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.saveOneTwoRepairCell(saveInfo);
            String msg = checklistTriggerUrlCallResult.getOperationResultMessage();
            if(StringUtils.isEmpty(msg)){
                msg = "保存记录单成功！";
            }
            return RestResult.success().setData(checklistTriggerUrlCallResult).setMsg(msg);
        } catch (Exception ex) {
            logger.error("/checkInfo/SaveOneTwoRepairCell---保存记录单接口出错...", ex);
            return RestResult.fromException(ex, logger, "保存记录单失败");
        }
    }

    @ApiOperation(value = "检修工人签字", notes = "检修工人签字")
    @PostMapping(value = "/repairPersonSign")
    @ResponseBody
    public RestResult repairPersonSign(@RequestBody ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.repairPersonSign(saveInfo);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/repairPersonSign----检修工人签字接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }

    @ApiOperation(value = "导入数据", notes = "导入数据")
    @PostMapping(value = {"/ImportData", "/ImportData"})
    @ResponseBody
    public RestResult ImportData(@RequestBody ChecklistSummary checklistSummary) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.ImportData(checklistSummary);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/ImportData----获取导入数据接口出错...", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        }
    }

    @ApiOperation(value = "获取更改的确认值单元格", notes = "获取更改的确认值单元格")
    @PostMapping(value = {"/changeStateCells", "/changeStateCells"})
    @ResponseBody
    public RestResult changeStateCells(@RequestBody ChecklistSummaryInfoForSave checklistSummary) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.changeStateCells(checklistSummary);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/changeStateCells----获取更改的确认值单元格接口出现异常...", ex);
            return RestResult.fromException(ex, logger, "获取更改的确认值单元格接口出现异常");
        }

    }

    @ApiOperation(value = "保存镟修数据", notes = "保存镟修数据")
    @PostMapping(value = {"/saveWheelDatas", "/saveWheelDatas"})
    @ResponseBody
    public RestResult saveWheelDatas(@RequestBody ChecklistSummaryInfoForSave checklistSummary) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.saveWheelDatas(checklistSummary);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/saveWheelDatas----保存镟修数据接口出现异常...", ex);
            return RestResult.fromException(ex, logger, "保存镟修数据接口出现异常");
        }
    }

    @ApiOperation(value = "设置检修人员信息", notes = "设置检修人员信息")
    @PostMapping(value = {"/setRepairPerson", "/setRepairPerson"})
    @ResponseBody
    public RestResult setRepairPerson(@RequestBody ChecklistSummaryInfoForSave checklistSummary) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.setRepairPerson(checklistSummary);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/setRepairPerson----设置检修人员信息接口出现异常...", ex);
            return RestResult.fromException(ex, logger, "设置检修人员信息接口出现异常");
        }
    }

    @ApiOperation(value = "确认人签字", notes = "确认人签字")
    @PostMapping(value = {"/confirmPerson", "/confirmPerson"})
    @ResponseBody
    public RestResult confirmPerson(@RequestBody ChecklistSummaryInfoForSave checklistSummary) {
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.confirmPerson(checklistSummary);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        } catch (Exception ex) {
            logger.error("/checkInfo/confirmPerson----确认人签字接口出现异常...", ex);
            return RestResult.fromException(ex, logger, "确认人签字接口出现异常");
        }
    }
    @ApiOperation(value = "机检技术人员签字", notes = "机检技术人员签字")
    @PostMapping(value = {"/signTechnique", "/signTechnique"})
    @ResponseBody
    public  RestResult signTechnique(@RequestBody  ChecklistSummaryInfoForSave checklistSummary){
        try{
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.signTechnique(checklistSummary);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        }catch (Exception ex){
            logger.error("/checkInfo/signTechnique----机检技术人员签字异常...", ex);
            return RestResult.fromException(ex, logger, "机检技术人员签字异常");
        }
    }
    @ApiOperation(value = "镟修探伤单据轴质检签字", notes = "镟修探伤单据轴质检签字")
    @PostMapping(value = {"/setAxleQualitySign", "/setAxleQualitySign"})
    @ResponseBody
    public RestResult setAxleQualitySign(@RequestBody ChecklistSummaryInfoForSave saveInfo){
        try{
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = checkInfoService.setAxleQualitySign(saveInfo);
            return RestResult.success().setData(checklistTriggerUrlCallResult);
        }catch (Exception ex){
            logger.error("/checkInfo/setAxleQualitySign----镟修探伤单据轴质检签字...", ex);
            return RestResult.fromException(ex, logger, "镟修探伤单据轴质检签字");
        }
    }
    @ApiOperation(value = "校验单据是否发生修改", notes = "校验单据是否发生修改")
    @PostMapping(value = {"/checkIsChangeed", "/checkIsChangeed"})
    @ResponseBody
    public RestResult checkIsChangeed(@RequestBody ChecklistSummaryInfoForSave saveInfo){
        try{
            String msg = checkInfoService.checkIsChangeed(saveInfo);
            return RestResult.success().setMsg(msg);
        }catch (Exception ex){
            logger.error("/checkInfo/checkIsChangeed----校验单据是否发生修改...", ex);
            return RestResult.fromException(ex, logger, "校验单据是否发生修改");
        }
    }
}
