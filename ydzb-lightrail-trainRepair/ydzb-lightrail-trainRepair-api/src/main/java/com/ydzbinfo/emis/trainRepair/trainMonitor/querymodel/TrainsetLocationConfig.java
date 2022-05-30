package com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author gaohan
 * @since 2021-03-01
 */
@TableName("XZY_C_TRAINSETLOCATION_CONFIG")
public class TrainsetLocationConfig implements Serializable {


    @TableId("S_ID")
    private String id;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 配置项
     */
    @TableField("S_PARAMNAME")
    private String paramName;

    /**
     * 配置值
     */
    @TableField("S_PARAMVALUE")
    private String paramValue;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return "TrainsetlocationConfig{" +
            "id=" + id +
            ", unitCode=" + unitCode +
            ", paramname=" + paramName +
            ", paramvalue=" + paramValue +
            ", unitName=" + unitName +
        "}";
    }
}
