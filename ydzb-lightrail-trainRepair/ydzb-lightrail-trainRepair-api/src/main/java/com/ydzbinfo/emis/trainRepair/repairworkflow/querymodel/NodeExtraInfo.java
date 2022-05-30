package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.IExtraInfoBase;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 节点额外信息表	如：是否可跳过、节点激活聚合条件、预警时间信息、动态节点额外条件、节点路由标识（主、次等等，在程序内使用枚举值定义，并由此在程序中定义并行节点的基础路由方法）等
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@TableName("XZY_C_NODEEXTRAINFO")
@Data
public class NodeExtraInfo implements Serializable, IExtraInfoBase {

    /**
     * 节点主键
     */
    @TableField("S_NODEID")
    private String nodeId;

    /**
     * 额外信息类型
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 额外信息值
     */
    @TableField("S_VALUE")
    private String value;

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

}
