package com.ydzbinfo.emis.common.bill.typeconfig.service;


import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeMode;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhaoxl
 * @since 2021-03-23
 */
public interface ITemplateAttributeService extends IService<TemplateAttribute> {

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
    List<TemplateAttributeForShow> getTemplateAttributeList(Page<TemplateAttributeForShow> page, String attributeTypeCode, String attributeName, String attributeCode,String sysType);

    /**
     * 删除一个属性
     */
    void delTemplateAttribute(String id);
    boolean delTemplateAttributeModel(TemplateAttribute templateAttribute);


    /**
     * 根据属性编码获取取值范围
     */
    List<TemplateValue> getTemplateValueList(String attributeCode);

    /**
     * 新增属性
     */
    Integer addTemplateAttribute(TemplateAttributeForSave templateAttribute);
    Integer addTemplateAttributeModel(TemplateAttributeForSave templateAttribute);

    /**
     * 新增模板取值范围（先删后插）
     */
    void setTemplateValues(List<TemplateValue> templateValueList,String attributeCode);

    /**
     * 更新属性
     * @param templateAttribute
     */
    Integer updateTemplateAttribute(TemplateAttributeForSave templateAttribute);
    Integer updateTemplateAttributeModel(TemplateAttributeForSave templateAttribute);

    TemplateAttributeForSave selectTemplateAttributeWithDetail(String id);
}
