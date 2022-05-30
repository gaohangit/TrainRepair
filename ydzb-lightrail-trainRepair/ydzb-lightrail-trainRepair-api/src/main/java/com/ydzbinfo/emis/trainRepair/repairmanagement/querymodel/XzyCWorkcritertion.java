package com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 作业标准配置（1级修）
 * </p>
 *
 * @author 
 * @since 2020-06-01
 */
@TableName("XZY_C_WORKCRITERTION")
public class XzyCWorkcritertion extends Model<XzyCWorkcritertion> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("S_CRITERTIONID")
    private String sCritertionid;
    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String sItemcode;
    /**
     * 车型
     */
    @TableField("S_TRAINSETTYPE")
    private String sTrainsettype;
    /**
     * 阶段
     */
    @TableField("S_TRAINSETSUBTYPE")
    private String sTrainsetsubtype;
    /**
     * 修程
     */
    @TableField("S_CYC")
    private String sCyc;
    /**
     * 供断电状态1有电 2无电
     */
    @TableField("S_POWERSTATE")
    private String sPowerstate;
    /**
     * 预警时长（分钟）
     */
    @TableField("S_WARNINGTIME")
    private Double sWarningtime;
    /**
     * 项目NAME
     */
    @TableField("S_ITEMNAME")
    private String sItemname;

    /**
     * 项目名称简称
     */
    @TableField("S_ITEMNAMEABBR")
    private String sItemnameAbbr;

    /**
     * 最小作业时长（分钟）
     */
    @TableField("I_MINWORKTIME")
    private Double iMinworktime;
    /**
     * 最大作业时长（分钟）
     */
    @TableField("I_MAXWORKTIME")
    private Double iMaxworktime;
    /**
     * 照片数量
     */
    @TableField("I_PICCOUNT")
    private Integer iPiccount;
    /**
     * 可用标志1为可用0为不可用
     */
    @TableField("C_FLAG")
    private String cFlag;
    /**
     * 动车段CODE
     */
    @TableField("S_DEPOTCODE")
    private String sDepotcode;
    /**
     * 动车段名称
     */
    @TableField("S_DEPOTNAME")
    private String sDepotname;
    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String sTrainsetid;
    /**
     * 车组NAME
     */
    @TableField("S_TRAINSETNAME")
    private String sTrainsetname;

    /**
     * 创建人编码
     */
    @TableField("S_CREATEUSERCODE")
    private String createUserCode;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String createUserName;

    /**
     * 创建人时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;


    /**
     * 删除人编码
     */
    @TableField("S_DELUSERCODE")
    private String delUserCode;

    /**
     * 删除人名称
     */
    @TableField("S_DELUSERNAME")
    private String delUserName;

    /**
     * 删除人时间
     */
    @TableField("D_DELTIME")
    private Date delTime;

    /**
     * 作业人数
     */
    @TableField("S_PERSONCOUNT")
    private Integer personCount;

    /**
     * 作业人数
     */
    @TableField("S_STUFFID")
    private String stuffId;

    /**
     * 预警角色集合
     */
    @TableField(exist = false)
    private List<XzyCWorkcritertionRole> xzyCWorkcritertionRoleList;

    /**
     * 岗位集合
     */
    @TableField(exist = false)
    private List<XzyCWorkcritertionPost> xzyCWorkcritertionPostList;

    /**
     * 优先角色集合
     */
    @TableField(exist = false)
    private List<XzyCWorkritertionPriRole> xzyCWorkritertionPriRoleList;

    public String getStuffId() {
        return stuffId;
    }

    public void setStuffId(String stuffId) {
        this.stuffId = stuffId;
    }

    public String getsCritertionid() {
        return sCritertionid;
    }

    public void setsCritertionid(String sCritertionid) {
        this.sCritertionid = sCritertionid;
    }

    public String getsItemcode() {
        return sItemcode;
    }

    public void setsItemcode(String sItemcode) {
        this.sItemcode = sItemcode;
    }

    public String getsTrainsettype() {
        return sTrainsettype;
    }

    public void setsTrainsettype(String sTrainsettype) {
        this.sTrainsettype = sTrainsettype;
    }

    public String getsTrainsetsubtype() {
        return sTrainsetsubtype;
    }

    public void setsTrainsetsubtype(String sTrainsetsubtype) {
        this.sTrainsetsubtype = sTrainsetsubtype;
    }

    public String getsCyc() {
        return sCyc;
    }

    public void setsCyc(String sCyc) {
        this.sCyc = sCyc;
    }

    public String getsPowerstate() {
        return sPowerstate;
    }

    public void setsPowerstate(String sPowerstate) {
        this.sPowerstate = sPowerstate;
    }

    public Double getsWarningtime() {
        return sWarningtime;
    }

    public void setsWarningtime(Double sWarningtime) {
        this.sWarningtime = sWarningtime;
    }

    public String getsItemname() {
        return sItemname;
    }

    public void setsItemname(String sItemname) {
        this.sItemname = sItemname;
    }

    public Double getiMinworktime() {
        return iMinworktime;
    }

    public void setiMinworktime(Double iMinworktime) {
        this.iMinworktime = iMinworktime;
    }

    public Double getiMaxworktime() {
        return iMaxworktime;
    }

    public void setiMaxworktime(Double iMaxworktime) {
        this.iMaxworktime = iMaxworktime;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    public String getsDepotcode() {
        return sDepotcode;
    }

    public void setsDepotcode(String sDepotcode) {
        this.sDepotcode = sDepotcode;
    }

    public String getsDepotname() {
        return sDepotname;
    }

    public void setsDepotname(String sDepotname) {
        this.sDepotname = sDepotname;
    }

    public String getsTrainsetid() {
        return sTrainsetid;
    }

    public void setsTrainsetid(String sTrainsetid) {
        this.sTrainsetid = sTrainsetid;
    }

    public String getsTrainsetname() {
        return sTrainsetname;
    }

    public void setsTrainsetname(String sTrainsetname) {
        this.sTrainsetname = sTrainsetname;
    }



    public List<XzyCWorkcritertionRole> getXzyCWorkcritertionRoleList() {
        return xzyCWorkcritertionRoleList;
    }

    public void setXzyCWorkcritertionRoleList(List<XzyCWorkcritertionRole> xzyCWorkcritertionRoleList) {
        this.xzyCWorkcritertionRoleList = xzyCWorkcritertionRoleList;
    }

    public List<XzyCWorkcritertionPost> getXzyCWorkcritertionPostList() {
        return xzyCWorkcritertionPostList;
    }

    public void setXzyCWorkcritertionPostList(List<XzyCWorkcritertionPost> xzyCWorkcritertionPostList) {
        this.xzyCWorkcritertionPostList = xzyCWorkcritertionPostList;
    }

    public List<XzyCWorkritertionPriRole> getXzyCWorkritertionPriRoleList() {
        return xzyCWorkritertionPriRoleList;
    }

    public void setXzyCWorkritertionPriRoleList(List<XzyCWorkritertionPriRole> xzyCWorkritertionPriRoleList) {
        this.xzyCWorkritertionPriRoleList = xzyCWorkritertionPriRoleList;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDelUserCode() {
        return delUserCode;
    }

    public void setDelUserCode(String delUserCode) {
        this.delUserCode = delUserCode;
    }

    public String getDelUserName() {
        return delUserName;
    }

    public void setDelUserName(String delUserName) {
        this.delUserName = delUserName;
    }

    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }

    public String getsItemnameAbbr() {
        return sItemnameAbbr;
    }

    public void setsItemnameAbbr(String sItemnameAbbr) {
        this.sItemnameAbbr = sItemnameAbbr;
    }

    public Integer getiPiccount() {
        return iPiccount;
    }

    public void setiPiccount(Integer iPiccount) {
        this.iPiccount = iPiccount;
    }

    public Integer getPersonCount() {
        return personCount;
    }

    public void setPersonCount(Integer personCount) {
        this.personCount = personCount;
    }

    @Override
    protected Serializable pkVal() {
        return this.sCritertionid;
    }

    @Override
    public String toString() {
        return "XzyCWorkcritertion{" +
                "sCritertionid='" + sCritertionid + '\'' +
                ", sItemcode='" + sItemcode + '\'' +
                ", sTrainsettype='" + sTrainsettype + '\'' +
                ", sTrainsetsubtype='" + sTrainsetsubtype + '\'' +
                ", sCyc='" + sCyc + '\'' +
                ", sPowerstate='" + sPowerstate + '\'' +
                ", sWarningtime=" + sWarningtime +
                ", sItemname='" + sItemname + '\'' +
                ", sItemnameAbbr='" + sItemnameAbbr + '\'' +
                ", iMinworktime=" + iMinworktime +
                ", iMaxworktime=" + iMaxworktime +
                ", iPiccount=" + iPiccount +
                ", cFlag='" + cFlag + '\'' +
                ", sDepotcode='" + sDepotcode + '\'' +
                ", sDepotname='" + sDepotname + '\'' +
                ", sTrainsetid='" + sTrainsetid + '\'' +
                ", sTrainsetname='" + sTrainsetname + '\'' +
                ", createUserCode='" + createUserCode + '\'' +
                ", createUserName='" + createUserName + '\'' +
                ", createTime=" + createTime +
                ", delUserCode='" + delUserCode + '\'' +
                ", delUserName='" + delUserName + '\'' +
                ", delTime=" + delTime +
                ", personCount=" + personCount +
                ", xzyCWorkcritertionRoleList=" + xzyCWorkcritertionRoleList +
                ", xzyCWorkcritertionPostList=" + xzyCWorkcritertionPostList +
                ", xzyCWorkritertionPriRoleList=" + xzyCWorkritertionPriRoleList +
                '}';
    }
}
