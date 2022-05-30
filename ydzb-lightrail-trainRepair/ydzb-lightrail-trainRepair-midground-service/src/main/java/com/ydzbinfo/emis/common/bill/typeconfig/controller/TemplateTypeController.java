package com.ydzbinfo.emis.common.bill.typeconfig.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.common.bill.typeconfig.service.ITemplateTypeService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.utils.RecFunctionUtil;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-25
 */
@RestController
@RequestMapping(TemplateTypeController.BASE_MAPPING)
public class TemplateTypeController {

    public static final String BASE_MAPPING = "/templateType";

    protected static final Logger logger = LoggerFactory.getLogger(TemplateTypeController.class);

    @Resource
    ITemplateTypeService iTemplateTypeService;

    /**
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description:
     */
    @ApiOperation("获取单据类型下拉列表（单据类型、子类型、单据名称）")
    @GetMapping(value = "/getTemplateType")
    public RestResult getTemplateType(@RequestParam("fatherCode") String fatherCode, @RequestParam("type") String type,@RequestParam("sysType") String sysType,@RequestParam("cFlag") String cFlag) {
        try {
            RestResult result = RestResult.success();
            List<TemplateType> templateTypeList = iTemplateTypeService.getTemplateType(fatherCode, type,sysType,cFlag);
            List<Map<String, String>> resList = new ArrayList<>();
            for (TemplateType item : templateTypeList) {
                Map<String, String> map = new HashMap<>();
                map.put("templateTypeCode", item.getTemplateTypeCode());
                map.put("templateTypeName", item.getTemplateTypeName());
                map.put("fatherTypeCode", item.getFatherTypeCode());
                map.put("type", item.getType());
                map.put("sysType", item.getSysType());
                map.put("sysTemplate", item.getSysTemplate());
                resList.add(map);
            }
            result.setData(resList);
            result.setMsg("获取成功");
            return result;
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取单据类型下拉列表失败");
        }
    }


    /**
     * @author: 韩旭
     * @Date: 2021/8/7
     * @Description: 获取类型集合下所有子类型
     * @modifier 张天可
     */
    @ApiOperation("获取类型集合下所有子类型（递归获取）")
    @PostMapping(value = "/getAllChildTemplateTypeList")
    public RestResult getAllChildTemplateTypeList(@RequestBody List<String> templateTypeCodes) {
        try {
            RestResult result = RestResult.success();
            Function<List<String>, List<TemplateType>> addChildren = RecFunctionUtil.makeRec((fatherTypeCodes, self) -> {
                List<TemplateType> allChildren = new ArrayList<>();
                fatherTypeCodes.forEach(v -> allChildren.addAll(iTemplateTypeService.getTemplateType(v, "","","1")));
                List<String> allChildrenTypeCodes = allChildren.stream().map(TemplateType::getTemplateTypeCode).collect(Collectors.toList());
                if (allChildrenTypeCodes.size() > 0) {
                    allChildren.addAll(self.apply(allChildrenTypeCodes, self));
                }
                return allChildren;
            });
            result.setData(addChildren.apply(templateTypeCodes));
            result.setMsg("获取成功");
            return result;
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取单据类型下拉列表失败");
        }
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description: 获取单据类型查询列表
     */
    @ApiOperation("获取单据类型查询列表")
    @PostMapping(value = "/getTemplateTypeList")
    @ResponseBody
    public RestResult getTemplateTypeList(@RequestBody TemplateTypeQueryParamModel paramModel) {
        try {
            Page<TemplateType> page = new Page<>(paramModel.getPageNum(), paramModel.getPageSize());

            List<TemplateTypeForShow> templateTypeList = iTemplateTypeService.getTemplateTypeList(page, paramModel);
            JSONObject resObj = new JSONObject();
            resObj.put("templateAttributeList", templateTypeList);
            resObj.put("count", page.getTotal());
            RestResult result = RestResult.success();
            result.setData(resObj);
            result.setMsg("获取成功");
            return result;
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取单据类型查询列表失败");
        }
    }


    /**
     * 获取记录单类型详情信息
     *
     * @author 张天可
     */
    @GetMapping("/getTemplateTypeInfo")
    @ResponseBody
    @ApiOperation(value = "获取记录单类型详情信息", notes = "获取记录单类型详情信息")
    @BussinessLog(value = "获取记录单类型详情信息", key = BASE_MAPPING + "/getTemplateTypeInfo", type = "04")
    public Object getTemplateTypeInfo(@RequestParam String templateTypeCode) {
        try {
            return RestResult.success().setData(iTemplateTypeService.getTemplateTypeInfo(templateTypeCode));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取记录单类型详情信息失败");
        }
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 获取关联条件集合
     */
    @ApiOperation("获取关联条件集合")
    @GetMapping(value = "/getQueryList")
    @ResponseBody
    public RestResult getQueryList() {
        try {
            logger.info("getQueryList----开始调用获取关联条件集合接口...");
            List<TemplateQuery> templateQueryList = iTemplateTypeService.getQueryList();
            return RestResult.success().setData(templateQueryList).setMsg("获取成功");
        } catch (Exception ex) {
            logger.info("getQueryList----调用获取关联条件集合接口失败...");
            logger.error("调用获取关联条件集合接口失败:", ex);
            return RestResult.fromException(ex, logger, "获取失败");
        } finally {
            logger.info("getQueryList----调用获取关联条件集合接口结束...");
        }
    }


    @Data
    public static class AddTemplateTypeModel {
        private List<TemplateTypeForSave> templateTypeList;
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 新增单据类型
     */
    @ApiOperation("新增单据类型")
    @PostMapping(value = "/addTemplateTypes")
    @ResponseBody
    // @Transactional(propagation = Propagation.REQUIRED)
    public RestResult addTemplateTypes(@RequestBody AddTemplateTypeModel addTemplateTypeModel) {
        logger.info("addTemplateType----开始调用新增单据类型接口...");
        try {
            List<TemplateTypeForSave> templateTypeList = addTemplateTypeModel.getTemplateTypeList();
            Integer resCount = iTemplateTypeService.addTemplateTypes(templateTypeList);
            return RestResult.success().setMsg("添加成功");
        } catch (Exception ex) {
            logger.error("ddTemplateType----调用新增单据类型接口失败:", ex);
            return RestResult.fromException(ex, logger, "添加失败");
        } finally {
            logger.info("addTemplateType----调用新增单据类型接口结束...");
        }
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 删除单据类型
     */
    @ApiOperation("删除单据类型")
    @GetMapping(value = "/delTemplateType")
    @ResponseBody
    public RestResult delTemplateType(@RequestParam("templateTypeCode") String templateTypeCode) {
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        try {
            logger.info("delTemplateType----开始调用删除单据类型接口...");
            List<String> delTypeCodes = new ArrayList<>();
            delTypeCodes.add(templateTypeCode);
            iTemplateTypeService.delTemplateTypeAndAttrAndQuery(delTypeCodes);
            // //删除单据类型
            // Integer delCount = iTemplateTypeService.delTemplateType(delTypeCodes, currentUser.getStaffId(), currentUser.getName());
            // //删除关联条件
            // iTemplateTypeService.delQuery(delTypeCodes, currentUser.getStaffId(), currentUser.getName());
            // //删除关联属性
            // iTemplateTypeService.delTemplateLinkAttrs(delTypeCodes, currentUser.getStaffId(), currentUser.getName());
            return RestResult.success().setMsg("删除成功");
        } catch (Exception ex) {
            logger.info("delTemplateType----调用删除单据类型接口失败...");
            logger.error("调用删除单据类型接口失败:", ex);
            return RestResult.fromException(ex, logger, "删除失败");
        } finally {
            logger.info("delTemplateType----调用删除单据类型接口结束...");
        }
    }

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 修改单据类型
     */
    @ApiOperation("修改单据类型")
    @PostMapping(value = "/updateTemplateType")
    @ResponseBody
    public RestResult updateTemplateType(@RequestBody TemplateTypeForSave templateType) {
        try {
            logger.info("updateTemplateType----开始调用修改单据类型接口...");
            Integer resCount = iTemplateTypeService.updateTemplateType(templateType);
            return RestResult.success().setMsg("修改成功");
        } catch (Exception ex) {
            logger.info("updateTemplateType----调用修改单据类型接口失败...");
            logger.error("调用修改单据类型接口失败:", ex);
            return RestResult.fromException(ex, logger, "修改失败");
        } finally {
            logger.info("updateTemplateType----调用修改单据类型接口结束...");
        }
    }

}
