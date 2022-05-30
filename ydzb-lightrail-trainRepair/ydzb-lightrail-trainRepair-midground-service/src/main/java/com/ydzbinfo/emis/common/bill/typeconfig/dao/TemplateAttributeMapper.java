package com.ydzbinfo.emis.common.bill.typeconfig.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeMode;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-23
 */
public interface TemplateAttributeMapper extends BaseMapper<TemplateAttribute> {

    /**
     * 获取属性类型下拉框数据
     */
    List<TemplateAttributeType> getAttributeTypeList();

    /**
     * 获取属性模式下拉框数据
     */
    List<TemplateAttributeMode> getAttributeModeList();

    /**
     * 获取属性列表数据
     */
    List<TemplateAttributeForShow> getTemplateAttributeList(@Param("page") Page<TemplateAttributeForShow> page, @Param("attributeTypeCode") String attributeTypeCode, @Param("attributeName") String attributeName, @Param("attributeCode") String attributeCode,@Param("sysType") String sysType);

    /**
     * 根据属性编码获取取值范围
     */
    List<TemplateValue> getTemplateValueList(@Param("attributeCode") String attributeCode);

    /**
     * 更新属性
     */
    Integer updateTemplateAttribute(TemplateAttribute templateAttribute);


    TemplateAttributeForSave selectTemplateAttributeWithDetail(String id);

    int delTemplateAttribute(String id);

    Integer getLinkAttrCountByAttributeCode(@Param("attributeCode") String attributeCode);

}
