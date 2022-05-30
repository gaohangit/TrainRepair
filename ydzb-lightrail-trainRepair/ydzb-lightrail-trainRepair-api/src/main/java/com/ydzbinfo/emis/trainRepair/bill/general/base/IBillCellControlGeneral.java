package com.ydzbinfo.emis.trainRepair.bill.general.base;

/**
 * @author 张天可
 * @since 2021/6/25
 */
public interface IBillCellControlGeneral {

    /**
     * 1--完全回填    2--回填条件  3--默认回填值
     */
    String getType();

    String getRowId();

    String getColId();

    Integer getAreaNumber();

    void setType(String type);

    void setRowId(String rowId);

    void setColId(String colId);

    void setAreaNumber(Integer areaNumber);
}
