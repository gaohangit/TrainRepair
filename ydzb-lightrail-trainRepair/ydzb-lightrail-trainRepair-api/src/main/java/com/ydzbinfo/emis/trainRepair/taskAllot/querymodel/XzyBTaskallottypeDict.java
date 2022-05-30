package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
@TableName("TI_B_ITEMTYPE_DICT")
public class XzyBTaskallottypeDict extends Model<XzyBTaskallottypeDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 类型CODE
     */
    @TableField("S_ITEMTYPECODE")
    private String sTaskallottypecode;
    /**
     * 类型名称
     */
    @TableField("S_ITEMTYPENAME")
    private String sTaskallottypename;
    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String sRemark;
    /**
     * 是否可用   1--可用   0-- 不可用
     */
    @TableField("C_FLAG")
    private String cFlag;


    public String getsTaskallottypecode() {
        return sTaskallottypecode;
    }

    public void setsTaskallottypecode(String sTaskallottypecode) {
        this.sTaskallottypecode = sTaskallottypecode;
    }

    public String getsTaskallottypename() {
        return sTaskallottypename;
    }

    public void setsTaskallottypename(String sTaskallottypename) {
        this.sTaskallottypename = sTaskallottypename;
    }

    public String getsRemark() {
        return sRemark;
    }

    public void setsRemark(String sRemark) {
        this.sRemark = sRemark;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    @Override
    protected Serializable pkVal() {
        return this.sTaskallottypecode;
    }

    @Override
    public String toString() {
        return "XzyBTaskallottypeDict{" +
        "sTaskallottypecode=" + sTaskallottypecode +
        ", sTaskallottypename=" + sTaskallottypename +
        ", sRemark=" + sRemark +
        ", cFlag=" + cFlag +
        "}";
    }
}
