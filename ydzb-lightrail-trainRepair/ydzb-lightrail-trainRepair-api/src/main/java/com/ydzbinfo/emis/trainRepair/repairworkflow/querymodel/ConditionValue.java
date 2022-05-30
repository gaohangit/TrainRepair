package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 条件值表
 * </p>
 *
 * @author zhangtk
 * @since 2021-04-09
 */
@TableName("XZY_C_CONDITIONVALUE")
public class ConditionValue implements Serializable {


    /**
     * 条件主键
     */
    @TableField("S_CONDITIONID")
    private String conditionId;

    /**
     * 条件值
     */
    @TableField("S_VALUE")
    private String value;

    /**
     * 条件值类型
     */
    @TableField("S_TYPE")
    private String type;

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

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSynFlag() {
        return synFlag;
    }

    public void setSynFlag(String synFlag) {
        this.synFlag = synFlag;
    }

    public Date getSynDate() {
        return synDate;
    }

    public void setSynDate(Date synDate) {
        this.synDate = synDate;
    }


    @Override
    public String toString() {
        return "ConditionValue{" +
            "conditionId=" + conditionId +
            ", value=" + value +
            ", type=" + type +
            ", synFlag=" + synFlag +
            ", synDate=" + synDate +
        "}";
    }
}
