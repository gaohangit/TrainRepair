package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateAreaBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateControlBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateLinkContentBase;

import java.util.List;

/**
 * 用于记录单配置展示的基本配置
 */
public interface ITemplateInfo<CONTENT extends ITemplateContentInfo<LINK, CTRL>, LINK extends ITemplateLinkContentBase, CTRL extends ITemplateControlBase, AREA extends ITemplateAreaBase> extends ITemplateBase {

    /**
     * 单元格数据
     */
    List<CONTENT> getContents();

    /**
     * 区域数据
     */
    List<AREA> getAreas();

    void setContents(List<CONTENT> contents);

    void setAreas(List<AREA> areas);
}
