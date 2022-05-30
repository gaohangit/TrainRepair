package com.ydzbinfo.emis.trainRepair.bill.general.base;

/**
 * @author 张天可
 * @since 2021/6/25
 */
public interface IBillCellInfoGeneral {
    String getRowId();

    String getColId();

    String getCode();

    String getValue();

    String getAttributeCode();

    void setRowId(String rowId);

    void setColId(String colId);

    void setCode(String code);

    void setValue(String value);

    void setAttributeCode(String attributeCode);
}
