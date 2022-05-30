package com.ydzbinfo.emis.trainRepair.bill.general.base;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

/**
 * @author 张天可
 * @since 2021/8/19
 */
@Data
public class BillCellInfoGeneral implements IBillCellInfoGeneral {
    /**
     * 数据行
     */
    @TableField("S_ROWID")
    private String rowId;
    /**
     * 数据列
     */
    @TableField("S_COLID")
    private String colId;
    /**
     * 单元格填写内容CODE
     */
    @TableField("S_CODE")
    private String code;
    /**
     * 单元格填写内容
     */
    @TableField("S_VALUE")
    private String value;
    /**
     * 属性编码
     */
    @TableField("S_ATTRIBUTECODE")
    private String attributeCode;
}
