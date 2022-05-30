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
@TableName("XZY_B_TASKALLOTSHOW_DICT")
public class XzyBTaskallotshowDict extends Model<XzyBTaskallotshowDict> {

    private static final long serialVersionUID = 1L;


    /**
     * 项目类型编码
     */
    private String itemTypeCode;

    /**
     * 显示方式CODE
     */
    @TableField("S_TASKALLOTSHOWCODE")
    private String sTaskallotshowcode;
    /**
     * 显示方式NAME
     */
    @TableField("S_TASKALLOTSHOWNAME")
    private String sTaskallotshowname;
    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String sRemark;
    /**
     * 是否可用  1--可用 0 --不可用
     */
    @TableField("C_FLAG")
    private String cFlag;


    public String getsTaskallotshowcode() {
        return sTaskallotshowcode;
    }

    public void setsTaskallotshowcode(String sTaskallotshowcode) {
        this.sTaskallotshowcode = sTaskallotshowcode;
    }

    public String getsTaskallotshowname() {
        return sTaskallotshowname;
    }

    public void setsTaskallotshowname(String sTaskallotshowname) {
        this.sTaskallotshowname = sTaskallotshowname;
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

    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    @Override
    protected Serializable pkVal() {
        return this.sTaskallotshowcode;
    }


    @Override
    public String toString() {
        return "XzyBTaskallotshowDict{" +
                "itemTypeCode='" + itemTypeCode + '\'' +
                ", sTaskallotshowcode='" + sTaskallotshowcode + '\'' +
                ", sTaskallotshowname='" + sTaskallotshowname + '\'' +
                ", sRemark='" + sRemark + '\'' +
                ", cFlag='" + cFlag + '\'' +
                '}';
    }
}
