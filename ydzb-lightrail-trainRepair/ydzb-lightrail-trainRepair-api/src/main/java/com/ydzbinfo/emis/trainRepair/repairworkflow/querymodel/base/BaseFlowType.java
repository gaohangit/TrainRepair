package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 张天可
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseFlowType {

    /**
     * 额外流程类型标识
     */
    @TableId("S_CODE")
    private String code;

    /**
     * 额外流程类型名称
     */
    @TableField("S_NAME")
    private String name;

    /**
     * 排序值
     */
    @TableField("I_SORT")
    private Integer sort;
}
