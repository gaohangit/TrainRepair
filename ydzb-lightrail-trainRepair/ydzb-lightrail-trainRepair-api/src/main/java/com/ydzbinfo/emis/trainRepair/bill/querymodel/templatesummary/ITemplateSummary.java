package com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.IConfigTemplateBase;

import java.util.Date;

public interface ITemplateSummary extends IConfigTemplateBase {
    String getPlatform();

    String getVersion();

    String getValidFlag();

    Date getValidDate();

    Date getUnvalidDate();

    String getDelFlag();

    String getPublish();

    String getSynFlag();

    Date getSynDate();
}
