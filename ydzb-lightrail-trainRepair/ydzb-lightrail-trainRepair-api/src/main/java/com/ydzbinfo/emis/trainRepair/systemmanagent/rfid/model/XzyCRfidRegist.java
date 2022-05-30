package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author yyx
 * @since 2020-04-29
 */
@TableName("XZY_C_RFIDREGIST")
public class XzyCRfidRegist extends Model<XzyCRfidRegist> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("S_ID")
    private String sId;
    /**
     * TID
     */
    @TableField("S_TID")
    private String sTid;
    /**
     * EPC
     */
    @TableField("S_EPC")
    private String sEpc;
    /**
     * 是否可用  0--不可用  1--可用
     */
    @TableField("C_FLAG")
    private String cFlag;
    /**
     * 股道CODE
     */
    @TableField("S_TRACKCODE")
    private String sTrackcode;

    //排序字段
    public static final String S_TRACKNAME = "S_TRACKNAME";
    /**
     * 股道名称
     */
    @TableField(S_TRACKNAME)
    private String sTrackname;

    /**
     * 列位名称
     */
    @TableField("S_PLACENAME")
    private String sPlaceName;

    /**
     * 列位code
     */
    @TableField("S_PLACECODE")
    private String sPlaceCode;

    /**
     * 作业位置CODE  01--地沟左侧  02--地沟右侧  03--车体左侧  04--车体右侧  05--车顶左侧  06--车顶右侧
     */
    @TableField("S_REPAIRPLACECODE")
    private String sRepairplacecode;

    //排序字段
    public static final String S_REPAIRPLACENAME = "S_REPAIRPLACENAME";
    /**
     * 作业位置名称
     */
    @TableField(S_REPAIRPLACENAME)
    private String sRepairplacename;

    //排序字段
    public static final String S_PILLARNAME = "S_PILLARNAME";
    /**
     * 立柱号
     */
    @TableField(S_PILLARNAME)
    private String sPillarname;

    public static final String D_CREATETIME = "D_CREATETIME";
    /**
     * 创建时间
     */
    @TableField(D_CREATETIME)
    private Date dCreatetime;
    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String sUnitcode;
    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String sUnitname;

    /**
     * 创建人编码
     */
    @TableField("S_CREATEUSERCODE")
    private String sCreateUserCode;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String sCreateUserName;

    /**
     * 删除人编码
     */
    @TableField("S_DELUSERCODE")
    private String sDelUserCode;

    /**
     * 删除人名称
     */
    @TableField("S_DELUSERNAME")
    private String sDelUserName;

    /**
     * 删除人时间
     */
    @TableField("D_DELTIME")
    private Date dDelTime;

    public String getsPlaceName() {
        return sPlaceName;
    }

    public void setsPlaceName(String sPlaceName) {
        this.sPlaceName = sPlaceName;
    }

    public String getsPlaceCode() {
        return sPlaceCode;
    }

    public void setsPlaceCode(String sPlaceCode) {
        this.sPlaceCode = sPlaceCode;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsTid() {
        return sTid;
    }

    public void setsTid(String sTid) {
        this.sTid = sTid;
    }

    public String getsEpc() {
        return sEpc;
    }

    public void setsEpc(String sEpc) {
        this.sEpc = sEpc;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    public String getsTrackcode() {
        return sTrackcode;
    }

    public void setsTrackcode(String sTrackcode) {
        this.sTrackcode = sTrackcode;
    }

    public String getsTrackname() {
        return sTrackname;
    }

    public void setsTrackname(String sTrackname) {
        this.sTrackname = sTrackname;
    }

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

    public String getsPillarname() {
        return sPillarname;
    }

    public void setsPillarname(String sPillarname) {
        this.sPillarname = sPillarname;
    }

    public Date getdCreatetime() {
        return dCreatetime;
    }

    public void setdCreatetime(Date dCreatetime) {
        this.dCreatetime = dCreatetime;
    }

    public String getsUnitcode() {
        return sUnitcode;
    }

    public void setsUnitcode(String sUnitcode) {
        this.sUnitcode = sUnitcode;
    }

    public String getsUnitname() {
        return sUnitname;
    }

    public void setsUnitname(String sUnitname) {
        this.sUnitname = sUnitname;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getsCreateUserCode() {
        return sCreateUserCode;
    }

    public void setsCreateUserCode(String sCreateUserCode) {
        this.sCreateUserCode = sCreateUserCode;
    }

    public String getsCreateUserName() {
        return sCreateUserName;
    }

    public void setsCreateUserName(String sCreateUserName) {
        this.sCreateUserName = sCreateUserName;
    }

    public String getsDelUserCode() {
        return sDelUserCode;
    }

    public void setsDelUserCode(String sDelUserCode) {
        this.sDelUserCode = sDelUserCode;
    }

    public String getsDelUserName() {
        return sDelUserName;
    }

    public void setsDelUserName(String sDelUserName) {
        this.sDelUserName = sDelUserName;
    }

    public Date getdDelTime() {
        return dDelTime;
    }

    public void setdDelTime(Date dDelTime) {
        this.dDelTime = dDelTime;
    }

    // public String getsCarNo() {
    //     return sCarNo;
    // }
    //
    // public void setsCarNo(String sCarNo) {
    //     this.sCarNo = sCarNo;
    // }

    @Override
    protected Serializable pkVal() {
        return this.sId;
    }


    @Override
    public String toString() {
        return "XzyCRfidRegist{" +
                "sId='" + sId + '\'' +
                ", sTid='" + sTid + '\'' +
                ", sEpc='" + sEpc + '\'' +
                ", cFlag='" + cFlag + '\'' +
                ", sTrackcode='" + sTrackcode + '\'' +
                ", sTrackname='" + sTrackname + '\'' +
                ", sRepairplacecode='" + sRepairplacecode + '\'' +
                ", sRepairplacename='" + sRepairplacename + '\'' +
                ", sPillarname='" + sPillarname + '\'' +
                ", dCreatetime=" + dCreatetime +
                ", sUnitcode='" + sUnitcode + '\'' +
                ", sUnitname='" + sUnitname + '\'' +
                ", sCreateUserCode='" + sCreateUserCode + '\'' +
                ", sCreateUserName='" + sCreateUserName + '\'' +
                ", sDelUserCode='" + sDelUserCode + '\'' +
                ", sDelUserName='" + sDelUserName + '\'' +
                ", dDelTime=" + dDelTime +
                '}';
    }
}
