package com.ydzbinfo.emis.trainRepair.common.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author 于雨新
 * @since 2020-05-18
 */
@Data
@TableName("XZY_C_CONFIG")
public class XzyCConfig {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId("S_ID")
    private String configId;
    /**
     * 类型
     */
    @TableField("S_TYPE")
    private String type;
    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String unitCode;
    /**
     * 参数名称
     */
    @TableField("S_PARAMNAME")
    private String paramName;
    /**
     * 参数值
     */
    @TableField("S_PARAMVALUE")
    private String paramValue;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    @Override
    public String toString() {
        return "XzyCConfig{" +
        "configId=" + configId +
        ", type=" + type +
        ", unitCode=" + unitCode +
        ", paramName=" + paramName +
        ", paramValue=" + paramValue +
        ", remark=" + remark +
        "}";
    }
}
