package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public class XzyCAllotpersonConfig extends Model<XzyCAllotpersonConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String sRepairpersonallotid;
    /**
     * 部门CODE
     */
    private String sDeptcode;
    /**
     * 部门名称
     */
    private String sDeptname;
    /**
     * 班组CODE
     */
    private String sBranchcode;
    /**
     * 班组名称
     */
    private String sBranchname;
    /**
     * 作业人员CODE
     */
    private String sWorkcode;
    /**
     * 作业人员名称
     */
    private String sWorkname;
    /**
     * 是否兼职  1--是  0--否
     */
    private String cParttime;
    /**
     * 是否可用  1--可用 0--不可用
     */
    private String cFlag;
    /**
     * 运用所编码
     */
    private String sUnitcode;
    /**
     * 排序ID
     */
    private String sSort;
    /**
     * 创建时间
     */
    private Date dCreatetime;
    /**
     * 创建人
     */
    private String sCreateusercode;
    /**
     * 创建人名称
     */
    private String sCreateusername;
    /**
     * 删除时间
     */
    private Date dDeltime;
    /**
     * 删除人
     */
    private String sDelusercode;
    /**
     * 删除人名称
     */
    private String sDelusername;


    public String getsRepairpersonallotid() {
        return sRepairpersonallotid;
    }

    public void setsRepairpersonallotid(String sRepairpersonallotid) {
        this.sRepairpersonallotid = sRepairpersonallotid;
    }

    public String getsDeptcode() {
        return sDeptcode;
    }

    public void setsDeptcode(String sDeptcode) {
        this.sDeptcode = sDeptcode;
    }

    public String getsDeptname() {
        return sDeptname;
    }

    public void setsDeptname(String sDeptname) {
        this.sDeptname = sDeptname;
    }

    public String getsBranchcode() {
        return sBranchcode;
    }

    public void setsBranchcode(String sBranchcode) {
        this.sBranchcode = sBranchcode;
    }

    public String getsBranchname() {
        return sBranchname;
    }

    public void setsBranchname(String sBranchname) {
        this.sBranchname = sBranchname;
    }

    public String getsWorkcode() {
        return sWorkcode;
    }

    public void setsWorkcode(String sWorkcode) {
        this.sWorkcode = sWorkcode;
    }

    public String getsWorkname() {
        return sWorkname;
    }

    public void setsWorkname(String sWorkname) {
        this.sWorkname = sWorkname;
    }

    public String getcParttime() {
        return cParttime;
    }

    public void setcParttime(String cParttime) {
        this.cParttime = cParttime;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    public String getsUnitcode() {
        return sUnitcode;
    }

    public void setsUnitcode(String sUnitcode) {
        this.sUnitcode = sUnitcode;
    }

    public String getsSort() {
        return sSort;
    }

    public void setsSort(String sSort) {
        this.sSort = sSort;
    }

    public Date getdCreatetime() {
        return dCreatetime;
    }

    public void setdCreatetime(Date dCreatetime) {
        this.dCreatetime = dCreatetime;
    }

    public String getsCreateusercode() {
        return sCreateusercode;
    }

    public void setsCreateusercode(String sCreateusercode) {
        this.sCreateusercode = sCreateusercode;
    }

    public String getsCreateusername() {
        return sCreateusername;
    }

    public void setsCreateusername(String sCreateusername) {
        this.sCreateusername = sCreateusername;
    }

    public Date getdDeltime() {
        return dDeltime;
    }

    public void setdDeltime(Date dDeltime) {
        this.dDeltime = dDeltime;
    }

    public String getsDelusercode() {
        return sDelusercode;
    }

    public void setsDelusercode(String sDelusercode) {
        this.sDelusercode = sDelusercode;
    }

    public String getsDelusername() {
        return sDelusername;
    }

    public void setsDelusername(String sDelusername) {
        this.sDelusername = sDelusername;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

    @Override
    public String toString() {
        return "XzyCAllotpersonConfig{" +
                "sRepairpersonallotid=" + sRepairpersonallotid +
                ", sDeptcode=" + sDeptcode +
                ", sDeptname=" + sDeptname +
                ", sBranchcode=" + sBranchcode +
                ", sBranchname=" + sBranchname +
                ", sWorkcode=" + sWorkcode +
                ", sWorkname=" + sWorkname +
                ", cParttime=" + cParttime +
                ", cFlag=" + cFlag +
                ", sUnitcode=" + sUnitcode +
                ", sSort=" + sSort +
                ", dCreatetime=" + dCreatetime +
                ", sCreateusercode=" + sCreateusercode +
                ", sCreateusername=" + sCreateusername +
                ", dDeltime=" + dDeltime +
                ", sDelusercode=" + sDelusercode +
                ", sDelusername=" + sDelusername +
                "}";
    }
}
