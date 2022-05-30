package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateBase;

/**
 * @author 张天可
 * @date 2021/6/18
 */
public interface ITemplateForSave extends ITemplateBase {

    /**
     * @return ssjson文件内容
     */
    String getSsjsonFile();

}
