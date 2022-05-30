package com.ydzbinfo.emis.common.bill.basetemplate.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.common.bill.basetemplate.service.IBaseTemplateService;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.trainRepair.bill.model.QueryBillBaseConfigModel;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateGroupForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.BaseTemplateUniqueKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplate;
import com.ydzbinfo.emis.trainRepair.constant.MarshallingTypeEnum;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.result.RestResultGeneric;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * @author 张天可
 * @since 2022/2/16
 */
@RestController(BaseTemplateController.BASE_MAPPING)
public class BaseTemplateController {

    public static final String BASE_MAPPING = "/billBaseTemplate";

    protected static final Logger logger = LoggerFactory.getLogger(BaseTemplateController.class);


    @Autowired
    private IBaseTemplateService baseTemplateService;

    /**
     * 查询基础模板详情
     */
    @GetMapping("/getBaseTemplateDetail")
    @ResponseBody
    public RestResultGeneric<List<BaseTemplateInfo>> getBaseTemplateDetail(@RequestParam("templateId") String templateId) {
        try {
            Set<String> templateIds = new HashSet<>(Arrays.asList(templateId.split(",")));
            List<BaseTemplate> baseTemplates = selectList(
                baseTemplateService,
                inParam(BaseTemplate::getTemplateId, templateIds)
            );
            return RestResultGeneric.success(baseTemplates.stream().map(baseTemplateService::transToBaseTemplateInfo).collect(Collectors.toList()));
        } catch (Exception ex) {
            return RestResultGeneric.fromException(ex, logger, null);
        }
    }

    /**
     * 查询记录单基础模板列表
     *
     * @author 张天可
     */
    @GetMapping(value = "/queryBaseTemplateList")
    @ApiOperation(value = "查询记录单基础模板", notes = "查询记录单基础模板")
    public RestResultGeneric<Page<BaseTemplateGroupForShow>> queryBaseTemplateList(QueryBillBaseConfigModel queryModel) {
        try {
            List<BaseTemplate> baseTemplates = selectList(
                baseTemplateService,
                MybatisPlusUtils.allMatch(
                    MybatisPlusUtils.getEqColumnParamsFromEntity(queryModel, BaseTemplateUniqueKey.class)
                ),
                StringUtils.isNotBlank(queryModel.getItemName()) ? likeIgnoreCaseParam(BaseTemplate::getItemName, queryModel.getItemName()) : null
            );
            // 根据模板编号分组展示
            List<BaseTemplateGroupForShow> baseTemplateGroupForShows = baseTemplates.stream()
                .collect(Collectors.groupingBy(BaseTemplate::getTemplateNo)).values().stream()
                .map(templates -> {
                    // 根据编组形式排序
                    templates.sort(
                        Comparator.comparing(BaseTemplate::getMarshallingType)
                    );
                    BaseTemplateGroupForShow baseTemplateGroupForShow = new BaseTemplateGroupForShow();
                    CommonUtils.copyPropertiesToChild(templates.get(0), BaseTemplate.class, baseTemplateGroupForShow, BaseTemplateGroupForShow.class);

                    baseTemplateGroupForShow.setTemplateId(
                        templates.stream().map(
                            BaseTemplate::getTemplateId
                        ).collect(Collectors.joining(","))
                    );

                    baseTemplateGroupForShow.setMarshallingTypeName(
                        templates.stream().map(
                            v -> MarshallingTypeEnum.getLabelByKey(v.getMarshallingType())
                        ).collect(Collectors.joining(","))
                    );
                    return baseTemplateGroupForShow;
                }).collect(Collectors.toList());

            // 先分页
            Page<BaseTemplateGroupForShow> page = CommonUtils.getPage(
                baseTemplateGroupForShows,
                queryModel.getPage(),
                queryModel.getLimit()
            );
            // 再查询类型名称，减少不必要的io操作
            page.getRecords().forEach(v -> {
                TemplateType templateType = BillUtil.queryTemplateTypeUseThreadCache(v.getTemplateTypeCode());
                if (templateType != null) {
                    v.setTemplateTypeName(templateType.getTemplateTypeName());
                }
            });
            return RestResultGeneric.success(page);
        } catch (Exception e) {
            return RestResultGeneric.fromException(e, logger, "查询记录单基础模板失败");
        }
    }

    /**
     * 保存基础模板
     *
     * @author 张天可
     */
    @PostMapping("/saveBaseTemplate")
    @ApiOperation(value = "保存基础模板", notes = "保存基础模板")
    public RestResultGeneric<List<String>> saveBaseTemplate(@RequestBody List<BaseTemplateForSave> templates) {
        try {
            return RestResultGeneric.success(baseTemplateService.saveBaseTemplates(templates));
        } catch (Exception e) {
            return RestResultGeneric.fromException(e, logger, "保存基础模板失败");
        }
    }

    /**
     * 移除基础模板
     *
     * @author 张天可
     */
    @GetMapping("/removeBaseTemplate")
    @ApiOperation(value = "移除基础模板", notes = "移除基础模板")
    public RestResult removeBaseTemplate(@RequestParam("templateId") String templateId) {
        try {
            Set<String> templateIds = new HashSet<>(Arrays.asList(templateId.split(",")));
            baseTemplateService.deleteByTemplateIds(templateIds);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "移除基础模板失败");
        }
    }
}
