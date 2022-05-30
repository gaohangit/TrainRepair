package com.ydzbinfo.emis.common.general.service.impl;


import com.ydzbinfo.emis.common.bill.typeconfig.dao.TemplateQueryMapper;
import com.ydzbinfo.emis.common.bill.typeconfig.service.ITemplateTypeService;
import com.ydzbinfo.emis.common.general.dao.CommonMapper;
import com.ydzbinfo.emis.common.general.service.ICommonService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeParamModel;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements ICommonService {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    TemplateQueryMapper templateQueryMapper;

    @Autowired
    ITemplateTypeService templateTypeService;

    @Override
    public List<TemplateType> getBillTypes(TemplateTypeParamModel templateTypeParamModel) {
        String fatherTypeCode = templateTypeParamModel.getFatherTypeCode();
        String levelType = templateTypeParamModel.getType();
        Predicate<TemplateType> levelTypeFilter = StringUtils.hasText(levelType) ? v -> Objects.equals(v.getType(), levelType) : (v) -> true;
        if (StringUtils.isEmpty(fatherTypeCode)) {
            return templateTypeService.getAllTemplateTypes(
                templateTypeParamModel.getShowSystemTemplate(),
                templateTypeParamModel.getShowCustomTemplate()
            ).stream().filter(levelTypeFilter).collect(Collectors.toList());
        } else {
            return templateTypeService.getAllChildTemplateTypes(
                fatherTypeCode,
                templateTypeParamModel.getShowSystemTemplate(),
                templateTypeParamModel.getShowCustomTemplate()
            ).stream().filter(levelTypeFilter).collect(Collectors.toList());
        }
    }

    @Override
    public List<XzyCConfig> getXzyCConfigs(ConfigParamsModel configParamsModel) {
        return commonMapper.getXzyCConfigs(configParamsModel);
    }

    @Override
    public List<TemplateQuery> getTemplateQueryList(String billTypeCode) {
        return commonMapper.getTemplateQueryList(billTypeCode);
    }
//
//	@Override
//	public List<TemplateLinkQuery> getTemplateLinkQuerys(Map<String, String> map) {
//		return commonMapper.getTemplateLinkQuerys(map);
//	}

    // @Override
    // public List<TemplateLinkAttrModel> getTemplateLinkAttrs(Map<String, String> map) {
    // 	return commonMapper.getTemplateLinkAttrs(map);
    // }

    @Override
    public List<TemplateAttributeTypeInfo> getTemplateAttributeTypes(String templateTypeCode) {
        return commonMapper.getTemplateAttributeTypesByTemplateType(templateTypeCode);
    }

    @Override
    public List<TemplateValue> getTemplateValues(String billTypeCode) {
        return commonMapper.getTemplateValues(billTypeCode);
    }

    // @Override
    // public List<TemplateType> getBillTypesByJoinOwn(String templateTypeCode) {
    //     return commonMapper.getBillTypesByJoinOwn(templateTypeCode);
    // }
}
