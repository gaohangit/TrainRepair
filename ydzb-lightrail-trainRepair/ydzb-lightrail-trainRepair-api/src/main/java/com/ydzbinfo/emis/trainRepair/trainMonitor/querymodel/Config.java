package com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author gaohan
 * @since 2021-03-03
 */
@TableName("XZY_C_CONFIG")
@Data
public class Config implements Serializable {

    @TableId("S_ID")
    private String id;

    @TableField("S_TYPE")
    private String type;

    @TableField("S_UNITCODE")
    private String unitCode;

    @TableField("S_PARAMNAME")
    private String paramName;

    @TableField("S_PARAMVALUE")
    private String paramValue;

    @TableField("S_REMARK")
    private String remark;

}
