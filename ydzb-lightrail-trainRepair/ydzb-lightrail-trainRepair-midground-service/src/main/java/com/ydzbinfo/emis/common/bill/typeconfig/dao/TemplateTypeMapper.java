package com.ydzbinfo.emis.common.bill.typeconfig.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-25
 */
public interface TemplateTypeMapper extends BaseMapper<TemplateType> {

    /**
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description: 获取单据类型下拉列表（单据类型、子类型、单据名称）
     */
    List<TemplateType> getTemplateType(@Param("fatherCode") String fatherCode, @Param("type") String type,@Param("sysType") String sysType,@Param("cFlag") String cFlag);

    /**
     * @author: fengshuai
     * @Date: 2021/4/12
     * @Description: 根据父类型集合获取单据类型集合
     */
    List<TemplateType> getTypeCodesByFatherCodes(@Param("list") List<String> fatherCodes);

    /**
     * @author: fengshuai
     * @Date: 2021/4/7
     * @Description: 获取单据类型查询列表
     */
    List<TemplateTypeForShow> getTemplateTypeList(Page page, TemplateTypeQueryParamModel paramModel);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 获取关联条件集合
     */
    List<TemplateQuery> getQueryList();

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 删除关联条件（逻辑删除）
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer delQuery(@Param("list") List<String> delTypeCodes,@Param("delUserCode") String delUserCode,@Param("delUserName") String delUserName);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 删除关联条件(物理删除)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer delQueryPhysics(@Param("list") List<String> delTypeCodes);


    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 批量添加关联条件
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer addQuerys(@Param("templateLinkQueryParamModelList") List<TemplateLinkQueryParamModel> templateLinkQueryParamModelList);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 批量新增单据类型
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer addTemplateTypes(@Param("list") List<TemplateType> templateTypeList);

    /**
     * @author: fengshuai
     * @Date: 2021/4/8
     * @Description: 删除单据类型
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer delTemplateType(@Param("list") List<String> delTypeCodes,@Param("delUserCode") String delUserCode,@Param("delUserName") String delUserName);

    /**
     * @author: fengshuai
     * @Date: 2021/4/9
     * @Description: 删除该模板类型的所有关联属性（逻辑删除）
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer delTemplateLinkAttrs(@Param("list") List<String> delTypeCodes,@Param("delUserCode") String delUserCode,@Param("delUserName") String delUserName);

    /**
     * @author: fengshuai
     * @Date: 2021/4/9
     * @Description: 删除该模板类型的所有关联属性（物理删除）
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer delTemplateLinkAttrsPhysics(@Param("list") List<String> delTypeCodes);
    /**
     * @author: fengshuai
     * @Date: 2021/4/9
     * @Description: 批量插入该模板类型关联属性
     */
    @Transactional(propagation = Propagation.REQUIRED)
    Integer addTemplateLinkAttrs(@Param("list") List<TemplateLinkAttrModel> templateLinkAttrModelList);


    List<TemplateType> getTemplatetypeFuList(TemplateType typeConfig);

    List<TemplateType> getTemplatetypeZiList(String tempId);

    List<TemplateType> getTemplatetypeNameList(String tempId);

    TemplateTypeInfo getTemplateTypeInfo(String templateTypeCode);
}
