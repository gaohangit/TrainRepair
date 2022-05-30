package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;

/**
 * @author 张天可
 * @since 2021/6/25
 */
public interface ITemplateControlBase extends IBillCellControlGeneral {
    String getId();

    String getContentId();

    void setId(String controlId);

    void setContentId(String contentId);

}
