package com.ydzbinfo.emis.common.general.service;


import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeParamModel;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;

import java.util.List;

public interface ICommonService {

    List<TemplateType> getBillTypes(TemplateTypeParamModel templateTypeParamModel);

    // // 获取单据类型 关联 单据类型本身的表
    // List<TemplateType> getBillTypesByJoinOwn(String templateTypeCode);

    List<XzyCConfig> getXzyCConfigs(ConfigParamsModel configParamsModel);

    // 模板类型查询条件字典表
    List<TemplateQuery> getTemplateQueryList(String billTypeCode);

    // 单据模板属性类型字典表
    List<TemplateAttributeTypeInfo> getTemplateAttributeTypes(String templateTypeCode);

    // 单据模板属性取值范围表
    List<TemplateValue> getTemplateValues(String billTypeCode);


}
