package com.ydzbinfo.emis.common.bill.typeconfig.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.ydzbinfo.emis.common.bill.typeconfig.service.ITemplateAttributeService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeVo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeMode;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;
import com.ydzbinfo.emis.trainRepair.common.model.Role;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.ServiceNameEnum;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-23
 */
@RestController
@RequestMapping("/templateAttribute")
public class TemplateAttributeController {

    protected static final Logger logger = LoggerFactory.getLogger(TemplateAttributeController.class);

    private final String userServiceId = ServiceNameEnum.UserService.getId();

    @Autowired
    ITemplateAttributeService iTemplateAttributeService;

    @Autowired
    IRemoteService remoteService;

    /**
     * @author: fengshuai
     * @Date: 2021/4/2
     * @Description:
     */
    @ApiOperation("获取单据-属性类型下拉框数据")
    @GetMapping(value = "/getTemplateAttributeTypeList")
    @ResponseBody
    public RestResult getTemplateAttributeTypeList() {
        RestResult result = RestResult.success();
        try {
            List<TemplateAttributeType> templateAttributeTypeList = iTemplateAttributeService.getAttributeTypeList();
            result.setData(templateAttributeTypeList);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/2
     * @Description:
     */
    @ApiOperation("获取单据-属性模式下拉框数据")
    @GetMapping(value = "getTemplateAttributeModeList")
    @ResponseBody
    public RestResult getTemplateAttributeModeList() {
        RestResult result = RestResult.success();
        try {
            List<TemplateAttributeMode> templateAttributeModeList = iTemplateAttributeService.getAttributeModeList();
            result.setData(templateAttributeModeList);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "获取单据属性列表数据", response = TemplateAttributeVo.class)
    @PostMapping(value = "/getTemplateAttributeList")
    @ResponseBody
    public RestResult getTemplateAttributeList(@RequestBody TemplateAttributeQueryParamModel paramModel) {
        try {
            Integer pageNum = paramModel.getPageNum();
            Integer pageSize = paramModel.getPageSize();
            if (pageSize == -1) {
                pageSize = Integer.MAX_VALUE;
            }
            Page<TemplateAttributeForShow> page = new Page<>(pageNum, pageSize);
            List<TemplateAttributeForShow> templateAttributeList = iTemplateAttributeService.getTemplateAttributeList(page, paramModel.getAttributeTypeCode(), paramModel.getAttributeName(), paramModel.getAttributeCode(), paramModel.getSysType());
            JSONObject resultData = new JSONObject();
            resultData.put("templateAttributeList", templateAttributeList);
            resultData.put("count", page.getTotal());
            return RestResult.success().setData(resultData);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取单据属性列表数据失败");
        }
    }


    @ApiOperation(value = "删除一条单据属性")
    @GetMapping(value = "/delTemplateAttribute")
    @ResponseBody
    public Object delTemplateAttribute(@RequestParam("id") String id) {
        try {
            iTemplateAttributeService.delTemplateAttribute(id);
            return RestResult.success().setMsg("删除成功");
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "删除一条单据属性失败");
        }
    }

    @ApiOperation(value = "根据属性编码获取取值范围")
    @GetMapping(value = "/getTemplateValueList")
    @ResponseBody
    public Object getTemplateValueList(@RequestParam("attributeCode") String attributeCode) {
        try {
            List<TemplateValue> templateValueList;
            templateValueList = iTemplateAttributeService.getTemplateValueList(attributeCode);
            List<Map<String, String>> resultList = new ArrayList<>();
            for (TemplateValue item : templateValueList) {
                Map<String, String> map = new HashMap<>();
                map.put("attributeCode", item.getAttributeCode());
                map.put("attributeRangeValue", item.getAttributeRangeValue());
                resultList.add(map);
            }
            return RestResult.success().setData(resultList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, null);
        }
    }


    @ApiOperation(value = "新增属性")
    @PostMapping(value = "/addTemplateAttribute")
    @ResponseBody
    public Object addTemplateAttribute(@RequestBody TemplateAttributeForSave templateAttribute) {
        try {
            if (templateAttribute.getAttributeCode() == null || templateAttribute.getAttributeCode().equals("")) {
                templateAttribute.setAttributeCode(IdWorker.get32UUID());
            }
            String id = IdWorker.get32UUID();
            templateAttribute.setId(id);
            iTemplateAttributeService.addTemplateAttribute(templateAttribute);
            return RestResult.success().setData(id);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "新增属性失败");
        }
    }

    @ApiOperation(value = "更新属性")
    @PostMapping(value = "/updateTemplateAttribute")
    @ResponseBody
    public Object updateTemplateAttribute(@RequestBody TemplateAttributeForSave templateAttribute) {
        try {
            Integer updateCount = iTemplateAttributeService.updateTemplateAttribute(templateAttribute);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "更新属性失败");
        }
    }

    @ApiOperation(value = "获取属性详情")
    @GetMapping(value = "/getTemplateAttributeWithDetail")
    @ResponseBody
    public Object getTemplateAttributeWithDetail(@RequestParam String id) {
        try {
            TemplateAttributeForSave templateAttribute = iTemplateAttributeService.selectTemplateAttributeWithDetail(id);
            return RestResult.success().setData(templateAttribute);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, null);
        }
    }

    @ApiOperation(value = "获取角色集合")
    @GetMapping(value = "/getRoles")
    public RestResult getRoles() {
        logger.info("getRoles----开始调用获取角色集合接口...");
        try {
            List<Role> roleList = remoteService.getAllRoleList();
            List<Map<String, Object>> res = roleList.stream().map(
                v -> {
                    Map<String, Object> mapItem = new HashMap<>();
                    mapItem.put("id", v.getCode());
                    mapItem.put("text", v.getName());
                    return mapItem;
                }
            ).collect(Collectors.toList());
            return RestResult.success().setData(res).setMsg("获取成功");
        } catch (Exception e) {
            logger.info("getRoles----调用获取角色集合接口失败...");
            logger.error("调用获取角色集合接口失败:", e);
            return RestResult.fromException(e, logger, "获取失败");
        } finally {
            logger.info("getRoles----调用获取角色集合接口结束...");
        }
    }

}
