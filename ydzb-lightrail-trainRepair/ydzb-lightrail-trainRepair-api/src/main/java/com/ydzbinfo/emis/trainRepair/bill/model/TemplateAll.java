package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ConfigTemplateBase;
import com.ydzbinfo.emis.trainRepair.constant.BillTemplateStateEnum;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 查询单据所有对象
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateAll extends ConfigTemplateBase implements ITemplateInfo<TemplateAllContent, TemplateAllLinkContent, TemplateAllControl, TemplateAllArea> {

    /**
     * 单据发布状态
     */
    private BillTemplateStateEnum state;

    /**
     * 单元格数据
     */
    private List<TemplateAllContent> contents;

    /**
     * 区域数据
     */
    private List<TemplateAllArea> areas;

    public String toString(){
        return ToStringUtil.toString(this);
    }
}
