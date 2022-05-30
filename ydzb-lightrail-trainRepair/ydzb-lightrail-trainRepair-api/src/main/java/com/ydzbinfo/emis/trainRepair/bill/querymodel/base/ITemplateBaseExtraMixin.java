package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;


/**
 * TemplateExtraMixin的实现接口
 *
 * @author 张天可
 * @date 2021/6/18
 */
public interface ITemplateBaseExtraMixin {

    String getTemplateId();

    void setTemplateId(String templateId);

    String getTemplateNo();

    void setTemplateNo(String templateNo);

    String getTemplateName();

    void setTemplateName(String templateName);

    String getRemark();

    void setRemark(String remark);

    String getItemName();

    void setItemName(String itemName);

    String getTemplatePath();

    void setTemplatePath(String templatePath);

    Integer getMarshallingType();

    void setMarshallingType(Integer marshallingType);
}
