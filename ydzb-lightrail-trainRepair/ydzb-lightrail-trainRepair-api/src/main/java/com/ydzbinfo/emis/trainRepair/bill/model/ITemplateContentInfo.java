package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateContentBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateControlBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateLinkContentBase;

import java.util.List;

/**
 * @author 张天可
 * @date 2021/6/22
 */
public interface ITemplateContentInfo<LINK extends ITemplateLinkContentBase, CTRL extends ITemplateControlBase> extends ITemplateContentBase {

    /**
     * 获取关联单元格信息
     *
     * @return
     */
    List<LINK> getLinkContents();

    /**
     * 获取控制信息
     *
     * @return
     */
    List<CTRL> getControls();

    void setLinkContents(List<LINK> linkContents);

    void setControls(List<CTRL> controls);
}
