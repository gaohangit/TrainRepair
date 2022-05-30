package com.ydzbinfo.emis.trainRepair.bill.model;


import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.IConfigTemplateBase;

public interface IQueryTemplateModel extends IConfigTemplateBase {
    String[] getTemplateTypeCodes();

    String[] getUnitCodes();

    String[] getBatches();

    String[] getTrainsetTypes();

    Boolean getShowSystemTemplate();

    Boolean getShowCustomTemplate();
}
