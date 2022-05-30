package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlow;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程表
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_FLOW")
@Data
public class Flow extends BaseFlow implements Serializable {

    /**
     * 是否可用(1 可用, 0 不可用), 用于临时保存尚未配置完整的流程
     */
    @TableField("C_USABLE")
    private String usable;

    /**
     * 是否删除(1 已删除, 0 未删除)
     */
    @TableField("C_DELETED")
    private String deleted;

    /**
     * 同步标识 0未同步 1已同步
     */
    @TableField("C_SYNFLAG")
    private String synFlag;

    /**
     * 同步时间
     */
    @TableField("D_SYNDATE")
    private Date synDate;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
