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
 * 额外流程类型表
 * </p>
 *
 * @author 张天可
 * @since 2021-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_C_EXTRAFLOWTYPE")
public class ExtraFlowType extends BaseFlowType implements Serializable {

    /**
     * 父流程类型标识
     */
    @TableField("S_PARENTFLOWTYPECODE")
    private String parentFlowTypeCode;

    /**
     * 流程配置类型 PACKET_NARROW:作业包范围收缩 PACKET_INDEPENDENT:作业包独立触发流程
     */
    @TableField("S_CONFIGTYPE")
    private String configType;

    /**
     * 注解描述
     */
    @TableField("S_COMMENT")
    private String comment;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

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

    /**
     * 页面类型，标记当前类型属于一级修作业还是二级修作业，如果父类型为一二级修，此字段起作用
     */
    @TableField("S_PAGETYPE")
    private String pageType;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
