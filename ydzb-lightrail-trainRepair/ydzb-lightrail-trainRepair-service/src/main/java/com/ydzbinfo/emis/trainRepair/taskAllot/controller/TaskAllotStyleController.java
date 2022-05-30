package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotStyle;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.ITaskAllotStyleService;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * @author gaohan
 * @description 显示方式
 * @createDate 2021/3/26 15:15
 **/
@RestController
@RequestMapping("taskAllotStyle")
public class TaskAllotStyleController {
    protected static final Logger logger = LoggerFactory.getLogger(TaskAllotStyleController.class);
    
    @Resource
    private ITaskAllotStyleService iTaskAllotStyleService;

    // @GetMapping(value = "/getTaskAllotStyle")
    // public Object getTaskAllotStyle(String model,String deptCode,String unitCode) {
    //     try{
    //         TaskAllotStyle task = new TaskAllotStyle();
    //         task.setMode(model);
    //         task.setDeptCode(deptCode);
    //         task.setUnitCode(unitCode);
    //         //先查
    //         TaskAllotStyle taskAllotStyle=iTaskAllotStyleService.getTaskAllotStyle(task);
    //         //如果有更改
    //         if(taskAllotStyle!=null){
    //             TaskAllotStyle upd = new TaskAllotStyle();
    //             upd.setId(taskAllotStyle.getId());
    //             upd.setMode(model);
    //             iTaskAllotStyleService.updateTaskAllotStyle(upd);
    //         }else{
    //             //如果没有新增
    //             TaskAllotStyle add = new TaskAllotStyle();
    //             add.setMode(model);
    //             add.setDeptCode(deptCode);
    //             add.setUnitCode(unitCode);
    //             UserShiroInfo userShiroInfo=UserShiroUtil.getUserInfo();
    //             add.setRecorderCode(UserShiroUtil.getUserInfo().getDeptCode());
    //             add.setRecorderName(UserShiroUtil.getUserInfo().getShortName());
    //             add.setRecordTime(new Date());
    //             iTaskAllotStyleService.addTaskAllotStyle(add);
    //
    //         }
    //         return RestResult.success();
    //     }catch (Exception e){
    //         return RestResult.fromException(e, logger, "获取其他车组(热备车)");
    //     }
    // }

    /**
     * 冯帅
     */
    @ApiOperation(value = "获取派工显示模式", notes = "获取派工显示模式")
    @GetMapping(value = "/getTaskAllotStyle")
    @ResponseBody
    public RestResult getTaskAllotStyle(@RequestParam("unitCode") String unitCode, @RequestParam("deptCode") String deptCode) {
        logger.info("/taskAllotStyle/getTaskAllotStyle----开始调用获取派工显示模式接口...");
        RestResult result = RestResult.success();
        try {
            List<TaskAllotStyle> taskAllotStyleList = MybatisPlusUtils.selectList(
                iTaskAllotStyleService,
                eqParam(TaskAllotStyle::getUnitCode, unitCode),
                eqParam(TaskAllotStyle::getDeptCode, deptCode)
            );
            result.setData(taskAllotStyleList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/taskAllotStyle/getTaskAllotStyle----调用获取派工显示模式接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/taskAllotStyle/getTaskAllotStyle----调用获取派工显示模式接口结束...");
        return result;
    }

    /**
     * 冯帅
     */
    @ApiOperation(value = "设置派工显示样式（先删后插）", notes = "设置派工显示样式（先删后插）")
    @PostMapping(value = "/setTaskAllotStyle")
    @ResponseBody
    public RestResult setTaskAllotStyle(@RequestBody TaskAllotStyle taskAllotStyle) {
        logger.info("/taskAllotStyle/setTaskAllotStyle----开始调用设置派工显示样式接口...");
        RestResult result = RestResult.success();
        try {
            String unitCode = taskAllotStyle.getUnitCode();
            String deptCode = taskAllotStyle.getDeptCode();
            if (unitCode == null || unitCode.equals("")) {
                throw new RuntimeException("运用所编码不能为空");
            }
            if (deptCode == null || deptCode.equals("")) {
                throw new RuntimeException("班组编码不能为空");
            }
            //获取当前登录用户
            ShiroUser currentUser = ShiroKit.getUser();
            //获取当前时间
            Date currentDate = new Date();
            //1.删除
            boolean delflag = MybatisPlusUtils.delete(
                iTaskAllotStyleService,
                eqParam(TaskAllotStyle::getUnitCode, taskAllotStyle.getUnitCode()),
                eqParam(TaskAllotStyle::getDeptCode, taskAllotStyle.getDeptCode())
            );
            //2.插入
            taskAllotStyle.setRecorderCode(currentUser.getStaffId());
            taskAllotStyle.setRecorderName(currentUser.getName());
            taskAllotStyle.setRecordTime(currentDate);
            taskAllotStyle.setId(UUID.randomUUID().toString());
            boolean addFlag = iTaskAllotStyleService.insert(taskAllotStyle);
            result.setMsg("成功");
        } catch (Exception ex) {
            logger.error("/taskAllotStyle/setTaskAllotStyle----调用设置派工显示样式接口出错...", ex);
            result = RestResult.fromException(ex, logger, "失败");
        }
        logger.info("/taskAllotStyle/setTaskAllotStyle----调用设置派工显示样式接口结束...");
        return result;
    }
}
