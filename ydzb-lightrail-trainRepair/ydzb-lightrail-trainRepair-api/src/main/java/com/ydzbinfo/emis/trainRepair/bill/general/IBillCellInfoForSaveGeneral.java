package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillLinkCellInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;

import java.util.List;

/**
 * 记录单回填，保存时从前端获取到的单元格数据实体类需要实现的接口
 * 保存用
 *
 * @author 张天可
 * @since 2021/6/25
 */
public interface IBillCellInfoForSaveGeneral<CTRL extends IBillCellControlGeneral, LINK_CELL extends IBillLinkCellInfoGeneral> extends IBillCellInfoGeneral {

    /**
     * 单元格信息id
     */
    String getId();

    void setId(String id);

    /**
     * 单元格内容变更类型
     */
    BillEntityChangeTypeEnum getChangeType();

    void setChangeType(BillEntityChangeTypeEnum changeType);

    /**
     * 关联单元格数据
     */
    List<LINK_CELL> getLinkCells();

    void setLinkCells(List<LINK_CELL> linkCells);

    /**
     * 回填控制数据
     */
    List<CTRL> getControls();

    void setControls(List<CTRL> controls);
}
