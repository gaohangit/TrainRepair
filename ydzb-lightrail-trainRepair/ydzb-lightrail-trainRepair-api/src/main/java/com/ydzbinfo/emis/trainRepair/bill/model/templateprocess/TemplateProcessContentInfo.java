package com.ydzbinfo.emis.trainRepair.bill.model.templateprocess;

import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcLinkContent;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessContent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-06-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateProcessContentInfo extends TemplateProcessContent implements Serializable, ITemplateContentInfo<TemplateProcLinkContent, TemplateProcLinkControl> {

    /**
     * 关联单元格数据
     */
    private List<TemplateProcLinkContent> linkContents;

    /**
     * 单元格控制数据
     */
    private List<TemplateProcLinkControl> controls;

}
