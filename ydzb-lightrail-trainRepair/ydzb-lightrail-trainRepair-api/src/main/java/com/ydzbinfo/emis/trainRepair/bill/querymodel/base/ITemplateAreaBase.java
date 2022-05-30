package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillAreaGeneral;

/**
 * @author 张天可
 * @since 2021/6/25
 */
public interface ITemplateAreaBase extends IBillAreaGeneral {
    String getId();

    String getTemplateId();

    void setId(String id);

    void setTemplateId(String templateId);

}
