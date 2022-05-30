package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlowType;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程类型表
 * </p>
 *
 * @author gaohan
 * @since 2021-05-21
 */
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_B_FLOWTYPE")
@Data
public class FlowType extends BaseFlowType implements Serializable {

    /**
     * 是否删除(1 已删除, 0 未删除)
     */
    @TableField("C_DELETED")
    private String deleted;

    /**
     * 删除时间
     */
    @TableField("D_DELETETIME")
    private Date deleteTime;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
