package com.ydzbinfo.emis.trainRepair.repairmanagent.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkQuery;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkVo;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyBCritertionDict;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyBPowerDict;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionRoleService;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskallotpacketService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * 控制器
 *
 * @author
 * @Date 2020-07-21 17:16:42
 * @modified_by 冯帅 2021年4月22日
 */
@Api(description = "作业标准配置")
@Controller
@RequestMapping("/xzyCWorkcritertion")
public class XzyCWorkcritertionEndpoint {

    protected static final Logger logger = LoggerFactory.getLogger(XzyCWorkcritertionEndpoint.class);

    @Autowired
    private IXzyCWorkcritertionService xzyCWorkcritertionService;
    @Autowired
    private IXzyCWorkcritertionRoleService xzyCWorkcritertionRoleService;


    //用户信息服务
    private final String UserServiceId = ServiceNameEnum.UserService.getId();

    //中台作业管理服务
    private final String RepairItemServiceId = ServiceNameEnum.RepairItemService.getId();

    @Resource
    IRemoteService remoteService;

    @Autowired
    IRepairMidGroundService midGroundService;

    @Autowired
    XzyMTaskallotpacketService xzyMTaskallotpacketService;

    /**
     * 标准修程作业过程管理-获取标准修程作业过程管理列表
     */
    @PostMapping(value = "/getWorkcritertionList")
    @ApiOperation(value = "标准修程作业过程管理", notes = "获取标准修程作业过程管理列表")
    @ResponseBody
    public Object getWorkcritertionList(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyCWorkcritertion/getWorkcritertionList----开始调用获取作业标准列表接口...");
        RestResult result = RestResult.success();
        try {
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            if (pageSize == -1) {
                pageNum = 1;
                pageSize = Integer.MAX_VALUE;
            }
            Page page = new Page(pageNum, pageSize);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("trainsetType", jsonObject.getString("trainsetType"));
            paramMap.put("trainsetSubType", jsonObject.getString("trainsetSubType"));
            paramMap.put("cyc", jsonObject.getString("cyc"));
            paramMap.put("itemName", jsonObject.getString("itemName"));
            //查询作业标准配置表
            List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionList(page, paramMap);
            JSONObject resObj = new JSONObject();
            resObj.put("xzyCWorkcritertionList", xzyCWorkcritertionList);
            resObj.put("count", page.getTotal());
            result.setData(resObj);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/getWorkcritertionList----调用获取作业标准列表接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyCWorkcritertion/getWorkcritertionList----调用获取作业标准列表接口结束...");
        return result;
    }

    /**
     * 检修作业标准配置新增
     *
     * @param
     * @return 成功或者失败
     */
    @ApiOperation(value = "检修作业标准配置-新增", notes = "检修作业标准配置-新增")
    @PostMapping(value = "/addWorkcritertion")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public Object addWorkcritertion(@RequestBody XzyCWorkcritertion xzyCWorkcritertion) {
        logger.info("/xzyCWorkcritertion/addWorkcritertion----开始调用新增作业标准接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            xzyCWorkcritertion.setsItemcode(UUID.randomUUID().toString());
            //验证当前配置是否存在
            List<XzyCWorkcritertion> queryList = MybatisPlusUtils.selectList(
                xzyCWorkcritertionService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(XzyCWorkcritertion::getsTrainsettype, xzyCWorkcritertion.getsTrainsettype()),
                    eqParam(XzyCWorkcritertion::getsTrainsetsubtype, xzyCWorkcritertion.getsTrainsetsubtype()),
                    eqParam(XzyCWorkcritertion::getsItemname, xzyCWorkcritertion.getsItemname()),
                    eqParam(XzyCWorkcritertion::getcFlag, "1")
                )
            );
            if (queryList.size() > 0) {
                throw RestRequestException.normalFail("当前标准已存在，不能重复添加！");
            }
            boolean addFlag = xzyCWorkcritertionService.addWorkcritertion(xzyCWorkcritertion);
            result.setMsg("新增成功");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/addWorkcritertion----调用新增作业标准接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        return result;
    }

    /**
     * 检修作业标准配置-修改
     *
     * @param
     * @return 成功或者失败
     */
    @ApiOperation(value = "检修作业标准配置-修改", notes = "检修作业标准配置-修改")
    @PostMapping(value = "/updateWorkcritertion")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public Object updateWorkcritertion(@RequestBody XzyCWorkcritertion xzyCWorkcritertion) {
        logger.info("/xzyCWorkcritertion/updateWorkcritertion----开始调用修改作业标准接口...");
        RestResult result = RestResult.success();
        try {
            //删除（假删除）
            List<String> delListIds = new ArrayList<>();
            delListIds.add(xzyCWorkcritertion.getsCritertionid());
            xzyCWorkcritertionService.delWorkcritertion(delListIds);
            //添加
            xzyCWorkcritertionService.addWorkcritertion(xzyCWorkcritertion);
            result.setMsg("修改成功");
            logger.info("/xzyCWorkcritertion/updateWorkcritertion----调用修改作业标准接口结束...");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/updateWorkcritertion----调用修改作业标准接口出错...", ex);
            result = RestResult.fromException(ex, logger, "修改失败");
        }
        return result;
    }

    /**
     * 一级修删除
     *
     * @return 成功或者失败
     */
    @ApiOperation(value = "一级修-删除", notes = "一级修-删除")
    @PostMapping(value = "/deleteWorkcritertion")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public Object deleteWorkcritertion(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyCWorkcritertion/deleteWorkcritertion----开始调用删除作业标准接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            JSONArray jsonArray = jsonObject.getJSONArray("xzyCWorkcritertionIds");
            List<String> delWorkcritertionIds = new ArrayList<>();
            if (!ObjectUtils.isEmpty(jsonArray)) {
                delWorkcritertionIds = jsonArray.toJavaList(String.class);
            }
            if (!CollectionUtils.isEmpty(delWorkcritertionIds)) {
                xzyCWorkcritertionService.delWorkcritertion(delWorkcritertionIds);
            }
            result.setMsg("删除成功");
            logger.info("/xzyCWorkcritertion/deleteWorkcritertion----调用删除作业标准接口结束...");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/deleteWorkcritertion----调用删除作业标准接口出错...", ex);
            result = RestResult.fromException(ex, logger, "删除失败");
        }
        return result;
    }

    @ApiOperation(value = "获取预警角色字典")
    @GetMapping(value = "/getCritertionDict")
    @ResponseBody
    public Object getCritertionDict() {
        logger.info("/xzyCWorkcritertion/getCritertionDict----开始调用获取预警角色下拉框接口...");
        RestResult result = RestResult.success();
        try {
            List<XzyBCritertionDict> xzyBCritertionDicts = new ArrayList<>();
            xzyBCritertionDicts = xzyCWorkcritertionService.getCritertionDict();
            result.setData(xzyBCritertionDicts);
            result.setMsg("获取成功");
        } catch (Exception e) {
            logger.error("/xzyCWorkcritertion/getCritertionDict----调用获取预警角色下拉框接口出错...", e);
            result = RestResult.fromException(e, logger, "获取失败");
        }
        logger.info("/xzyCWorkcritertion/getCritertionDict----调用获取预警角色下拉框接口结束...");
        return result;
    }

    @ApiOperation(value = "获取供断电状态字典")
    @GetMapping(value = "/getPowerStateDict")
    @ResponseBody
    public Object getPowerStateDict() {
        logger.info("/xzyCWorkcritertion/getCritertionDict----开始调用获取供断电下拉框接口...");
        RestResult result = RestResult.success();
        try {
            List<XzyBPowerDict> xzyBPowerDicts = new ArrayList<>();
            xzyBPowerDicts = xzyCWorkcritertionService.getPowerStateDict();
            result.setData(xzyBPowerDicts);
            result.setMsg("获取成功");
        } catch (Exception e) {
            logger.error("/xzyCWorkcritertion/getCritertionDict----调用获取供断电下拉框接口出错...", e);
            result = RestResult.fromException(e, logger, "获取失败");
        }
        logger.info("/xzyCWorkcritertion/getCritertionDict----调用获取供断电下拉框接口结束...");
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/4/26 14:23
     * @Description: 获取二级修项目
     */
    @PostMapping("/getSecondWorkcritertionList")
    @ResponseBody
    public Object getSecondWorkcritertionList(@RequestBody JSONObject jsonObject) {
        logger.info("/xzyCWorkcritertion/getSecondWorkcritertionList----开始调用获取二级修项目接口...");
        RestResult result = RestResult.success();
        Integer count = 0;
        JSONObject resObj = new JSONObject();
        try {
            //0.返回值
            List<Map<String, Object>> resultData = new ArrayList<>();
            //1.获取当前用户的段单位信息
            SysOrgan depotOrgan = ShiroKit.getUser().getDepotOrgan();
            //2.组织查询条件
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("page", jsonObject.getString("pageNum"));
            paramMap.put("limit", jsonObject.getString("pageSize"));
            paramMap.put("itemName", jsonObject.getString("itemName"));
            paramMap.put("trainType", jsonObject.getString("trainsetType"));
            paramMap.put("trainBatch", jsonObject.getString("trainsetSubType"));
            paramMap.put("useUnitCode", depotOrgan == null ? "" : depotOrgan.getOrganCode());
            //3.获取派工配置二级修是否启用新项目
            boolean useNewItem = ConfigUtil.isUseNewItem();
            //4从库中取二级修配置
            paramMap.put("cyc", "2");
            Page page = new Page(jsonObject.getInteger("pageNum"), jsonObject.getInteger("pageSize"));
            List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionList(page, paramMap);
            if (useNewItem) {//启用了新项目配置
                //5.1调用中台接口获取二级修检修项目
                RepairItemWorkQuery queryModel = new RepairItemWorkQuery();
                queryModel.setPage(jsonObject.getInteger("pageNum"));
                queryModel.setLimit(jsonObject.getInteger("pageSize"));
                queryModel.setItemName(jsonObject.getString("itemName"));
                queryModel.setTrainType(jsonObject.getString("trainsetType"));
                queryModel.setTrainSubType(jsonObject.getString("trainsetSubType"));
                queryModel.setDepotCode(depotOrgan == null ? "" : depotOrgan.getOrganCode());
                List<RepairItemWorkVo> resList = new ArrayList<>();
                Page<RepairItemWorkVo> repairItemWorkVoPage = remoteService.selectRepairItemListByWorkParam(queryModel);
                if (repairItemWorkVoPage != null && repairItemWorkVoPage.getRecords() != null) {
                    resList = repairItemWorkVoPage.getRecords();
                    count = resList.size();
                }
                //5.3组装数据
                if (!CollectionUtils.isEmpty(resList)) {
                    resultData = resList.stream().map(resItem -> {
                        Map<String, Object> map = new HashMap<>();
                        //5.4获取二级修项目配置
                        XzyCWorkcritertion xzyCWorkcritertion = new XzyCWorkcritertion();
                        if (CollectionUtils.isEmpty(xzyCWorkcritertionList)) {
                            List<XzyCWorkcritertion> filterList = xzyCWorkcritertionList.stream().filter(t -> t.getsItemcode().equals(resItem.getItemCode())).
                                filter(t -> t.getsTrainsettype().equals(resItem.getTrainType())).
                                filter(t -> t.getsTrainsetsubtype().equals(resItem.getTrainBatch())).collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(filterList)) {
                                xzyCWorkcritertion = filterList.get(0);
                            }
                        }
                        if (StringUtils.isNoneBlank(xzyCWorkcritertion.getsCritertionid())) {
                            xzyCWorkcritertion.setsCritertionid("");
                            xzyCWorkcritertion.setiPiccount(0);
                        }
                        map.put("sCritertionid", xzyCWorkcritertion.getsCritertionid());
                        map.put("sTrainsettype", resItem.getTrainType());
                        map.put("sTrainsetsubtype", resItem.getTrainBatch());
                        map.put("sItemcode", resItem.getItemCode());
                        map.put("sItemname", resItem.getItemName());
                        map.put("trainsetName", resItem.getTrainSetName());
                        map.put("notTrainsetName", resItem.getNotTrainSetName());
                        map.put("iPiccount", xzyCWorkcritertion.getiPiccount());
                        map.put("unitCode", resItem.getUnitCode());
                        map.put("sCyc", "2");
                        return map;
                    }).collect(Collectors.toList());
                }
                resObj.put("xzySecondCWorkcritertionList", resultData);
            } else {
                List<XzyCWorkcritertion> resultTwo = new ArrayList<>();
                //6.1获取旧项目
                Page<RepairItemVo> repairItemVoPage = new Page<>(1, 10000);
                List<RepairItemVo> repairItemVos = xzyMTaskallotpacketService.selectRepairItemList(paramMap, repairItemVoPage);

                String queryTrainType = jsonObject.getString("trainsetType");
                String queryTrainTempId = jsonObject.getString("trainsetSubType");
                String queryItemName = jsonObject.getString("itemName");

                if (StringUtils.isNotBlank(queryItemName)) {
                    repairItemVos = repairItemVos.stream().filter(v -> v.getItemName().contains(queryItemName)).collect(Collectors.toList());
                }
                //过滤车型
                if (StringUtils.isNotBlank(queryTrainType)) {
                    repairItemVos = repairItemVos.stream().filter(v -> v.getTrainsetType().equals(queryTrainType)).collect(Collectors.toList());
                }
                for (RepairItemVo repairItemVo : repairItemVos) {
                    List<XzyCWorkcritertion> filterType = xzyCWorkcritertionList.stream().filter(v -> v.getsTrainsettype().equals(repairItemVo.getTrainsetType()) && v.getcFlag().equals("1")).collect(Collectors.toList());
                    List<String> trainTempIds = CacheUtil.getDataUseThreadCache(
                        "remoteService.getTrainsetListReceived_" + repairItemVo.getTrainsetType(),
                        () -> remoteService.getPatchListByTraintype(repairItemVo.getTrainsetType())
                    );
                    if (StringUtils.isNotBlank(queryTrainTempId)) {
                        trainTempIds = trainTempIds.stream().filter(v -> v.equals(queryTrainTempId)).collect(Collectors.toList());
                    }
                    trainTempIds.forEach(v -> {
                        XzyCWorkcritertion xzyCWorkcritertion = new XzyCWorkcritertion();
                        xzyCWorkcritertion.setsTrainsettype(repairItemVo.getTrainsetType());
                        xzyCWorkcritertion.setsItemname(repairItemVo.getItemName());
                        xzyCWorkcritertion.setsItemcode(repairItemVo.getItemCode());
                        xzyCWorkcritertion.setsTrainsetsubtype(v);
                        xzyCWorkcritertion.setsCyc("2");
                        if (filterType != null) {
                            List<XzyCWorkcritertion> filterList = filterType.stream().filter(t -> t.getsItemcode().equals(repairItemVo.getItemCode())).
                                filter(t -> t.getsTrainsetsubtype().equals(v)).collect(Collectors.toList());
                            if (filterList.size() > 0) {
                                XzyCWorkcritertion queryXzyCWorkcritertion = filterList.get(0);
                                xzyCWorkcritertion.setsCritertionid(queryXzyCWorkcritertion.getsCritertionid());
                                xzyCWorkcritertion.setiPiccount(queryXzyCWorkcritertion.getiPiccount());
                            } else {
                                xzyCWorkcritertion.setiPiccount(0);
                            }
                            resultTwo.add(xzyCWorkcritertion);
                        }
                    });
                }
                count = resultTwo.size();
                resObj.put("xzySecondCWorkcritertionList", CommonUtils.getPage(resultTwo, jsonObject.getInteger("pageNum"), jsonObject.getInteger("pageSize")).getRecords());
            }
            resObj.put("count", count);
            result.setData(resObj);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/getSecondWorkcritertionList----调用获取二级修项目接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyCWorkcritertion/getSecondWorkcritertionList----调用获取二级修项目接口结束...");
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/4/27 11:22
     * @Description: 修改二级修项目图片数量
     */
    @PostMapping(value = "/updateSecondWorkcriterion")
    @ResponseBody
    public Object updateSecondWorkcriterion(@RequestBody XzyCWorkcritertion xzyCWorkcritertion) {
        logger.info("/xzyCWorkcritertion/updateSecondWorkcriterion----开始调用修改二级修项目图片数量接口...");
        RestResult result = RestResult.success();
        try {
            //删除
            if (!ObjectUtils.isEmpty(xzyCWorkcritertion.getsCritertionid())) {
                List<String> delIds = new ArrayList<>();
                delIds.add(xzyCWorkcritertion.getsCritertionid());
                xzyCWorkcritertionService.delWorkcritertion(delIds);
            }
            //插入
            xzyCWorkcritertionService.addWorkcritertion(xzyCWorkcritertion);
            result.setMsg("修改成功");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/updateSecondWorkcriterion----调用修改二级修项目图片数量接口出错...", ex);
            result = RestResult.fromException(ex, logger, "修改失败");
        }
        logger.info("/xzyCWorkcritertion/updateSecondWorkcriterion----调用修改二级修项目图片数量接口结束...");
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/4/27 15:56
     * @Description: 清空二级修图片数量（设置为0）
     */
    @GetMapping(value = "delSecondWorkcriterion")
    @ResponseBody
    public Object delSecondWorkcriterion(@RequestParam("sCritertionid") String sCritertionid) {
        logger.info("/xzyCWorkcritertion/delSecondWorkcriterion----开始调用清空二级修图片数量接口...");
        RestResult result = RestResult.success();
        try {
            //删除
            if (!ObjectUtils.isEmpty(sCritertionid)) {
                List<String> delIds = new ArrayList<>();
                delIds.add(sCritertionid);
                xzyCWorkcritertionService.delWorkcritertion(delIds);
            }
            result.setMsg("清空成功");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/delSecondWorkcriterion----调用清空二级修图片数量接口出错...", ex);
            result = RestResult.fromException(ex, logger, "清空失败");
        }
        logger.info("/xzyCWorkcritertion/delSecondWorkcriterion----调用清空二级修图片数量接口结束...");
        return result;
    }


    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 根据车组ID获取作业标准
     */
    @ApiOperation(value = "根据车组ID获取作业标准", notes = "根据车组ID获取作业标准")
    @RequestMapping(value = "/getWorkcritertionLByTrainsetId")
    @ResponseBody
    public RestResult getWorkcritertionLByTrainsetId(@RequestParam("trainsetId") String trainsetId) {
        logger.info("/xzyCWorkcritertion/getWorkcritertionLByTrainsetId----开始调用根据车组ID获取作业标准接口...");
        RestResult result = RestResult.success();
        try {
            List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetId(trainsetId);
            result.setData(xzyCWorkcritertionList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/getWorkcritertionLByTrainsetId----调用根据车组ID获取作业标准接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyCWorkcritertion/getWorkcritertionLByTrainsetId----调用根据车组ID获取作业标准接口结束...");
        return result;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 根据车组ID获取作业标准
     */
    @ApiOperation(value = "根据车组ID获取作业标准", notes = "根据车组ID获取作业标准")
    @RequestMapping(value = "/getWorkcritertionLByTrainsetIdOne")
    @ResponseBody
    public RestResult getWorkcritertionLByTrainsetIdOne(@RequestParam("trainsetId") String trainsetId) {
        logger.info("/xzyCWorkcritertion/getWorkcritertionLByTrainsetId----开始调用根据车组ID获取作业标准接口...");
        RestResult result = RestResult.success();
        try {
            List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetIdOne(trainsetId);
            result.setData(xzyCWorkcritertionList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/xzyCWorkcritertion/getWorkcritertionLByTrainsetId----调用根据车组ID获取作业标准接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/xzyCWorkcritertion/getWorkcritertionLByTrainsetId----调用根据车组ID获取作业标准接口结束...");
        return result;
    }
}
