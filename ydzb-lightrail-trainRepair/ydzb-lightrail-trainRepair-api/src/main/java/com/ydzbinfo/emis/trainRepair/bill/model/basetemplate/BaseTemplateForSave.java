package com.ydzbinfo.emis.trainRepair.bill.model.basetemplate;

import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateForSave;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author 张天可
 * @date 2021/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseTemplateForSave extends BaseTemplateInfo implements ITemplateForSave {


    /**
     * ssjson文件内容
     */
    String ssjsonFile;

}
