package com.ydzbinfo.emis.trainRepair.mobile.oftenfunction.controller;


import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.mobile.model.HomeAllotTask;
import com.ydzbinfo.emis.trainRepair.mobile.oftenfunction.service.IPhoneOftenFunctionService;
import com.ydzbinfo.emis.trainRepair.mobile.querymodel.PhoneOftenFunction;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 韩旭
 * @since 2021-04-07
 * @updateBy wuyuechang
 */
@RestController
@RequestMapping("/mobile")
public class PhoneOftenFunctionController {

    protected static final Logger logger = LoggerFactory.getLogger(PhoneOftenFunctionController.class);
    
    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    @Autowired
    private IPhoneOftenFunctionService iPhoneOftenFunctionService;

    @ApiOperation(value = "获取移动终端常用功能")
    @GetMapping(value = "/getOftenFunction", produces = "text/plain")
    public String getOftenFunction(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            logger.info("获取移动终端常用功能入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String staffId = jsonObject.getString("staffId");
            String type = jsonObject.getString("type");
            List<String> moduleIDList = iPhoneOftenFunctionService.getOftenFunction(staffId,type);
            logger.trace("获取移动终端常用功能出参:{}", moduleIDList);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", moduleIDList);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, logger, "获取移动终端常用功能数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @ApiOperation(value = "设置移动终端常用功能")
    @PostMapping(value = "/setOftenFunction", produces = "text/plain")
    public String setOftenFunction(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            logger.info("设置移动终端常用功能入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String staffId = jsonObject.getString("staffId");
            String type = jsonObject.getString("type");
            ShiroUser user = ShiroKit.getUser();
            List<String> moduleIDList = jsonObject.getJSONArray("moduleIDList").toJavaList(String.class);
            //获取当前操作人
            String name = user.getName();
            String code = user.getDepotOrgan().getOrganCode();
            //删除
            PhoneOftenFunction delModel = new PhoneOftenFunction();
            delModel.setFlag("0");
            delModel.setDelUserCode(code);
            delModel.setDelUserName(name);
            delModel.setDelTime(new Date());
            MybatisPlusUtils.update(
                iPhoneOftenFunctionService,
                delModel,
                eqParam(PhoneOftenFunction::getStaffId, staffId),
                eqParam(PhoneOftenFunction::getType, type)
            );
            //添加
            List<PhoneOftenFunction> addList = new ArrayList<>();
            for(String id:moduleIDList){
                PhoneOftenFunction addModel = new PhoneOftenFunction();
                addModel.setFlag("1");
                addModel.setStaffId(staffId);
                addModel.setType(type);
                addModel.setPhoneModuleId(id);
                addModel.setCreateTime(new Date());
                addModel.setCreateUserCode(code);
                addModel.setCreateUserName(name);
                addModel.setId(UUID.randomUUID().toString());
                addList.add(addModel);
            }
            iPhoneOftenFunctionService.insertBatch(addList);
            // iPhoneOftenFunctionService.setOftenFunction(staffId, name, code,moduleIDList);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", "");
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, logger, "获取设置移动终端常用功能数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
      * @author: wuyuechang
      * @Description: 查询登录人的检修任务
      * @param: 日计划id 登录人id
      * @return 登录人的检修任务
      * @date 2021.4.22
      */
    @ApiOperation(value = "查询登录人的检修任务")
    @PostMapping(value = "/getMobileLoginUserRepairTask", produces = "text/plain")
    public String getMobileLoginUserRepairTask(@RequestParam String RWMT) {
        long startNow = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        try{
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            logger.info("查询登录人的检修任务入参:{}", key);
            JSONObject jsonObject = JSONObject.parseObject(key);
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanID = jsonObject.getString("dayPlanID");
            String deptCode = jsonObject.getString("deptCode");
            String staffID = jsonObject.getString("staffID");
            List<String> trainsetList = new ArrayList<>();
            HomeAllotTask data = iPhoneOftenFunctionService.getMobileLoginUserRepairTask(unitCode,dayPlanID,"",trainsetList,deptCode,staffID);
            logger.trace("查询登录人的检修任务出参:{}", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
            result.put("data", data);
            result.put("msg", "成功");
        }catch (Exception e){
            RestResult restResult = RestResult.fromException(e, logger, "获取当前登录人的检修任务数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        long endNow = System.currentTimeMillis();
        logger.info("查询登录人的检修任务执行时间:{}", endNow - startNow);
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

}
