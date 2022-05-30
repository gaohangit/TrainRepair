package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseNode;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 节点表
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_NODE")
@Data
public class Node extends BaseNode implements Serializable {

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
