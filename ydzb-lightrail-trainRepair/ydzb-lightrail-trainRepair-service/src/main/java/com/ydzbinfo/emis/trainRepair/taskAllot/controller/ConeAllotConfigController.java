package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSource;
import com.ydzbinfo.emis.trainRepair.taskAllot.constant.TaskAllotConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotCarConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotTemplate;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyCOneallotConfigService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyCOneallotTemplateService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/16 12:16
 **/
@RestController
@RequestMapping("/xzyCOneallotConfig")
public class ConeAllotConfigController extends BaseController {

    @Autowired
    IXzyCOneallotConfigService xzyCOneallotConfigService;
    @Autowired
    IXzyCOneallotTemplateService xzyCOneallotTemplateService;

    @Autowired
    IXzyCOneallotTemplateService oneallotTemplateService;

    @Autowired(required = false)
    private TaskAllotConfigMqSource taskAllotConfigSource;


    @PostMapping(value = "/getOneAllotConfig")
    @ApiOperation("获取一级修派工配置")
    public List<XzyCOneallotConfig> getOneAllotConfig(@RequestBody Map<String, Object> map) {
        List<XzyCOneallotConfig> xzyCOneallotConfigList = xzyCOneallotConfigService.getOneAllotConfig(map);
        for (XzyCOneallotConfig oneallotConfig : xzyCOneallotConfigList) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("groupId", oneallotConfig.getsGroupid());
            List<XzyCOneallotTemplate> oneallotTemplateList = xzyCOneallotTemplateService.getTemplateListByParam(paramMap);
            oneallotConfig.setOneallotTemplateList(oneallotTemplateList);
        }
        return xzyCOneallotConfigList;
    }

    /**
     * @author: duanzefan
     * @Date: 2020/9/24 16:32
     * @Description: 修改一级修派工配置
     */
    @RequestMapping(value = "/setOneAllotConfig")
    @ResponseBody
    @ApiOperation("修改一级修派工配置")
    @BussinessLog(value = "修改一级修派工配置", key = "/xzyCOneallotConfig/setOneAllotConfig", type = "04")
    public int setOneAllotConfig(@RequestBody XzyCOneallotConfig oneallotConfig) {
        return xzyCOneallotConfigService.setOneAllotConfig(oneallotConfig);
    }

    /**
     *
     */
    @RequestMapping(value = "/getOneAllotConfigs")
    @ResponseBody
    @ApiOperation("获取一级修派工配置")
    @BussinessLog(value = "获取一级修派工配置", key = "/xzyCOneallotConfig/getOneAllotConfigs", type = "04")
    public List<XzyCOneallotCarConfig> getOneAllotConfigs(@RequestBody Map<String, String> map) {
        List<XzyCOneallotCarConfig> xzyCOneallotConfigList = xzyCOneallotConfigService.getOneAllotConfigs(map);
        return xzyCOneallotConfigList;
    }


    /**
     *
     */
    @RequestMapping(value = "/getOneAllotTemplates")
    @ResponseBody
    @ApiOperation("获取模版辆序")
    @BussinessLog(value = "获取模版辆序", key = "/xzyCOneallotConfig/getOneAllotTemplates", type = "04")
    public List<XzyCOneallotTemplate> getOneAllotTemplates(@RequestBody Map<String, String> map) {
        List<XzyCOneallotTemplate> xzyCOneallotConfigList = xzyCOneallotConfigService.getOneAllotTemplates(map);
        return xzyCOneallotConfigList;
    }

    /**
     * @author: duanzefan
     * @Date: 2020/9/14 11:11
     * @Description: 查询一级修配置
     */
    @ApiOperation("查询一级修配置")
    @RequestMapping(value = "/getOneAllotConfig")
    @ResponseBody
    @BussinessLog(value = "查询一级修配置", key = "/xzyCOneallotConfig/getOneAllotConfig", type = "04")
    public Map getOneAllotConfig(String unitCode, String deptCode) {
        List<XzyCOneallotConfig> xzyCOneallotConfigList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        MultiValueMap<String, Object> queryMap = new LinkedMultiValueMap<>();
        Map<String, Object> result = new HashMap<>();
        try {
            if (!ObjectUtils.isEmpty(unitCode) && !ObjectUtils.isEmpty(deptCode)) {
                map.put("unitCode", unitCode);
                map.put("deptCode", deptCode);
                queryMap.setAll(map);
                xzyCOneallotConfigList = xzyCOneallotConfigService.getOneAllotConfig(map);
                boolean needUpdate = false;
                if (xzyCOneallotConfigList == null || xzyCOneallotConfigList.size() == 0) {
                    needUpdate = true;
                    //如果配置表中没查出数据，则取template表中的默认数据
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("default", "1");
                    List<XzyCOneallotTemplate> oneallotTemplateList = oneallotTemplateService.getTemplateListByParam(paramMap);
                    //遍历template数据并重新组合
                    for (int i = 0; i < oneallotTemplateList.size(); i++) {
                        //第一次直接新建config实体，并把template放进去
                        if (i == 0) {
                            XzyCOneallotConfig oneallotConfig = new XzyCOneallotConfig();
                            List<XzyCOneallotTemplate> oneallotTemplates = new ArrayList<>();
                            oneallotTemplates.add(oneallotTemplateList.get(0));
                            oneallotConfig.setcFlag("1");
                            oneallotConfig.setsUnitcode(unitCode);
                            oneallotConfig.setsDeptcode(deptCode);
                            oneallotConfig.setsMarshalnum(oneallotTemplateList.get(i).getsMarshalnum());
                            oneallotConfig.setsGroupid(oneallotTemplateList.get(i).getsGroupid());
                            oneallotConfig.setOneallotTemplateList(oneallotTemplates);
                            xzyCOneallotConfigList.add(oneallotConfig);
                            //如果不是第一次，则有两种情况：1，groupId跟上一次相同
                            //遍历config集合，取出与本次遍历template的groupId相同的config，将本条template放入该config的子集中
                        } else if (oneallotTemplateList.get(i).getsGroupid().equals(oneallotTemplateList.get(i - 1).getsGroupid())) {
                            for (XzyCOneallotConfig oneallotConfig : xzyCOneallotConfigList) {
                                if (oneallotConfig.getsGroupid().equals(oneallotTemplateList.get(i).getsGroupid())) {
                                    oneallotConfig.getOneallotTemplateList().add(oneallotTemplateList.get(i));
                                }
                            }
                            //2,groupId跟上一次不同
                            //新建config
                        } else if (!oneallotTemplateList.get(i).getsGroupid().equals(oneallotTemplateList.get(i - 1).getsGroupid())) {
                            XzyCOneallotConfig oneallotConfig = new XzyCOneallotConfig();
                            List<XzyCOneallotTemplate> oneallotTemplates = new ArrayList<>();
                            oneallotTemplates.add(oneallotTemplateList.get(i));
                            oneallotConfig.setcFlag("1");
                            oneallotConfig.setsUnitcode(unitCode);
                            oneallotConfig.setsDeptcode(deptCode);
                            oneallotConfig.setsMarshalnum(oneallotTemplateList.get(i).getsMarshalnum());
                            oneallotConfig.setsGroupid(oneallotTemplateList.get(i).getsGroupid());
                            oneallotConfig.setOneallotTemplateList(oneallotTemplates);
                            xzyCOneallotConfigList.add(oneallotConfig);
                        }
                    }
                }
                for (XzyCOneallotConfig xzyCOneallotConfig : xzyCOneallotConfigList) {
                    if (needUpdate) {
                        updateOneAllotConfig(xzyCOneallotConfig);
                    }
                    xzyCOneallotConfig.setSort(Integer.valueOf(xzyCOneallotConfig.getsMarshalnum()));
                    //获取默认配置
                    List<XzyCOneallotTemplate> oneallotTemplates = oneallotTemplateService.getAllTemplateByMarshalNum(xzyCOneallotConfig.getsMarshalnum());
                    if (oneallotTemplates != null && oneallotTemplates.size() > 0) {
                        oneallotTemplates = oneallotTemplates.stream().filter(t -> t.getsGroupid().equals(xzyCOneallotConfig.getsGroupid())).collect(Collectors.toList());
                        xzyCOneallotConfig.setOneallotTemplateList(oneallotTemplates);
                    }
                }
                Collections.sort(xzyCOneallotConfigList, Comparator.comparing(XzyCOneallotConfig::getSort));
            }
            result.put("data", xzyCOneallotConfigList);
            result.put("code", 1);
            result.put("msg", "查询成功");
        } catch (Exception e) {
            result.put("code", 0);
            result.put("msg", "查询失败");
            logger.error(e.getMessage());
        }
        return result;
    }

    /**
     * @author: duanzefan
     * @Date: 2020/9/24 16:13
     * @Description: 修改一级修配置
     */
    @ApiOperation("修改一级修配置")
    @RequestMapping(value = "/updateOneAllotConfig")
    @ResponseBody
    @BussinessLog(value = "修改一级修配置", key = "/xzyCOneallotConfig/updateOneAllotConfig", type = "04")
    public Map updateOneAllotConfig(@RequestBody XzyCOneallotConfig oneallotConfig) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isEmpty(oneallotConfig.getsConfigid())) {
            oneallotConfig.setsConfigid(UUID.randomUUID().toString());
        }
        Integer res = xzyCOneallotConfigService.setOneAllotConfig(oneallotConfig);
        //往kafka中推送数据
        if (SpringCloudStreamUtil.enableSendCloudData(TaskAllotConfigMqSource.class)) {
            MessageWrapper<XzyCOneallotConfig> messageWrapper = new MessageWrapper<>(TaskAllotConfigHeaderConstant.class, TaskAllotConfigHeaderConstant.ONECREATE, oneallotConfig);
            boolean sendFlag = MessageUtil.sendMessage(taskAllotConfigSource.taskallotoneconfigChannel(), messageWrapper);
        }
        result.put("code", res);
        return result;
    }
}
