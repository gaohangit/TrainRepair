package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 条件表
 * </p>
 *
 * @author zhangtk
 * @since 2021-04-09
 */
@TableName("XZY_C_CONDITION")
public class Condition implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 条件类型(1 流程)
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 是否删除(1 已删除, 0 未删除)
     */
    @TableField("C_DELETED")
    private String deleted;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
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
        return "ConDitIon{" +
            "id=" + id +
            ", type=" + type +
            ", deleted=" + deleted +
            ", unitCode=" + unitCode +
            ", synFlag=" + synFlag +
            ", synDate=" + synDate +
        "}";
    }
}
