package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.IExtraInfoBase;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 节点记录额外信息表	比如：图片信息、表单信息等
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@TableName("XZY_M_NODERECORDEXTRAINFO")
@Data
public class NodeRecordExtraInfo implements Serializable, IExtraInfoBase {


    /**
     * 节点打卡记录主键
     */
    @TableField("S_NODERECORDID")
    private String nodeRecordId;

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

}
