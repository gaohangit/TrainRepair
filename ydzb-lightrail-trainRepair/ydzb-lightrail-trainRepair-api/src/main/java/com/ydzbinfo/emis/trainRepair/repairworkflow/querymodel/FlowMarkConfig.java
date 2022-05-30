package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 流程标记配置表，目前仅支持临修作业的关键字配置
 * </p>
 *
 * @author zhangtk
 * @since 2021-04-30
 */
@TableName("XZY_C_FLOWMARKCONFIG")
public class FlowMarkConfig implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 标记值
     */
    @TableField("S_MARK")
    private String mark;

    /**
     * 标记类型(1-临修作业关键字, 2-关键作业项目类型)
     */
    @TableField("S_TYPE")
    private String type;

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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return "FlowMarkConfig{" +
            "id=" + id +
            ", mark=" + mark +
            ", type=" + type +
            ", unitCode=" + unitCode +
            ", synFlag=" + synFlag +
            ", synDate=" + synDate +
        "}";
    }
}
