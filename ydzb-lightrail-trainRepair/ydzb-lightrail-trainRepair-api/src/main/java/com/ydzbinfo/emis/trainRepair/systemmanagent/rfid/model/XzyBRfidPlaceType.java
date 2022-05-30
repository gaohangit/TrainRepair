package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yyx
 * @since 2020-04-29
 */
@TableName("XZY_B_RFIDPLACE_TYPE")
public class XzyBRfidPlaceType extends Model<XzyBRfidPlaceType> {

    private static final long serialVersionUID = 1L;

    /**
     * 作业位置CODE  01--地沟左侧  02--地沟右侧  03--车体左侧  04--车体右侧  05--车顶左侧  06--车顶右侧
     */
    @TableId("S_REPAIRPLACECODE")
    private String sRepairplacecode;
    /**
     * 作业位置名称
     */
    @TableField("S_REPAIRPLACENAME")
    private String sRepairplacename;


    public String getsRepairplacecode() {
        return sRepairplacecode;
    }

    public void setsRepairplacecode(String sRepairplacecode) {
        this.sRepairplacecode = sRepairplacecode;
    }

    public String getsRepairplacename() {
        return sRepairplacename;
    }

    public void setsRepairplacename(String sRepairplacename) {
        this.sRepairplacename = sRepairplacename;
    }

    @Override
    protected Serializable pkVal() {
        return this.sRepairplacecode;
    }

    @Override
    public String toString() {
        return "XzyBRfidplaceType{" +
        "sRepairplacecode=" + sRepairplacecode +
        ", sRepairplacename=" + sRepairplacename +
        "}";
    }
}
