package com.ydzbinfo.emis.trainRepair.bill.general.base;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

/**
 * @author 张天可
 * @since 2021/6/25
 */
@Data
public class BillCellControlGeneral implements IBillCellControlGeneral {

    /**
     * 1--完全回填    2--回填条件  3--默认回填值
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 回填条件横坐标
     */
    @TableField("S_ROWID")
    private String rowId;

    /**
     * 回填条件纵坐标
     */
    @TableField("S_COLID")
    private String colId;

    /**
     * 回填条件区域编号
     */
    @TableField("I_AREANUMBER")
    private Integer areaNumber;
}
