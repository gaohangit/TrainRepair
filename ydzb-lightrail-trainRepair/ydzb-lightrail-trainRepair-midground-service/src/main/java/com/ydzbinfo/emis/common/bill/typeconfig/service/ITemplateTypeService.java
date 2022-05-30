package com.ydzbinfo.emis.common.bill.typeconfig.service;


import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-25
 */
public interface ITemplateTypeService extends IService<TemplateType> {

    /**
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description: 获取单据类型下拉列表（单据类型、子类型、单据名称）
     */
    List<TemplateType> getTemplateType(String fatherCode,String type,String sysType,String cFlag);

    /**
     * @author: 冯帅
     * @Date: 2022/3/24
     * @Description: 删除单据类型,及其相关的关联属性和关联条件（逻辑删除）
     */
    void delTemplateTypeAndAttrAndQuery(List<String> delTypeCodes);

    /**
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description: 获取单据类型查询列表
     * @return
     * @modify 张天可
     */
    List<TemplateTypeForShow> getTemplateTypeList(Page<TemplateType> page, TemplateTypeQueryParamModel paramModel);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 获取关联条件集合
     */
    List<TemplateQuery> getQueryList();

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 删除关联条件
     */
    Integer delQuery(List<String> delTypeCodes,String delUserCode,String delUserName);


    /**
     * @author: 冯帅
     * @Date: 2022/3/24
     * @Description: 删除关联条件
     */
    void delTemplateLinkQueryList(Map<String,String> delTypeCodeMap);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 新增单据类型
     * @param templateTypeList
     */
    Integer addTemplateTypes(List<TemplateTypeForSave> templateTypeList);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 删除单据类型
     */
    Integer delTemplateType(List<String> delTypeCodes,String delUserCode,String delUserName);

    /**
     * @author: 冯帅
     * @Date: 2022/3/24
     * @Description: 删除单据类型表数据
     */
    void delTemplateTypeList(Map<String,String> delTypeCodeMap);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 修改单据类型
     * @param templateType
     */
    Integer updateTemplateType(TemplateTypeForSave templateType);

    /**
     * @author: fengshuai
     * @Date: 2021/4/9
     * @Description: 删除该模板类型的所有关联属性
     */
    Integer delTemplateLinkAttrs(List<String> delTypeCodes,String delUserCode,String delUserName);

    /**
     * @author: 冯帅
     * @Date: 2022/3/24
     * @Description: 删除该模板类型的所有关联属性
     */
    void delTemplateLinkAttrList(Map<String,String> delTypeCodeMap);


    TemplateTypeInfo getTemplateTypeInfo(String templateTypeCode);

    List<TemplateType> getAllTemplateTypesIncludeSelf(String templateTypeCode, boolean showSystemTemplate, boolean showCustomTemplate);

    List<TemplateType> getAllChildTemplateTypes(String templateTypeCode, boolean showSystemTemplate, boolean showCustomTemplate);

    List<TemplateType> getAllTemplateTypes(boolean showSystemTemplate, boolean showCustomTemplate);
}
