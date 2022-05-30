package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author tzt
 * @since 2020-09-03
 */
@TableName("PZ_M_REPAIRTASK_ITEM")
public class PzMRepairtaskItem extends Model<PzMRepairtaskItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键/任务ID
     */
    @TableId("S_TASKITEMID")
    private String sTaskitemid;
    /**
     * 任务包ID
     */
    @TableField("S_TASKID")
    private String sTaskid;
    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String sTrainsetid;
    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String sTrainsetname;
    /**
     * 班次
     */
    @TableField("S_DAYPLANID")
    private String sDayplanid;
    /**
     * 配件ID
     */
    @TableField("S_PARTID")
    private String sPartid;
    /**
     * 生成时间
     */
    @TableField("D_CREATDATE")
    private String dCreatdate;
    /**
     * 项目编码(源编码)
     */
    @TableField("S_ITEMCODE")
    private String sItemcode;
    /**
     * 项目名称
     */
    @TableField("S_ITEMNAME")
    private String sItemname;
    /**
     * 项目类型
     */
    @TableField("S_ITEMTYPE")
    private String sItemtype;
    /**
     * 车辆号
     */
    @TableField("S_CAR")
    private String sCar;
    /**
     * 功能位编码
     */
    @TableField("S_POSITIONCODE")
    private String sPositioncode;
    /**
     * 功能位名称
     */
    @TableField("S_POSITIONNAME")
    private String sPositionname;
    /**
     * 位置编码
     */
    @TableField("S_LOCATIONCODE")
    private String sLocationcode;
    /**
     * 位置名称
     */
    @TableField("S_LOCATIONNAME")
    private String sLocationname;
    /**
     * 关键配件类型编码
     */
    @TableField("S_KEYPARTTYPEID")
    private String sKeyparttypeid;
    /**
     * 实体件ID
     */
    @TableField("S_MODELID")
    private String sModelid;
    /**
     * 实体件名称
     */
    @TableField("S_MODELNAME")
    private String sModelname;
    /**
     * 检修开始时间
     */
    @TableField("D_BEGINDATE")
    private String dBegindate;
    /**
     * 检修结束时间
     */
    @TableField("D_ENDDATE")
    private String dEnddate;
    /**
     * 走形公里数
     */
    @TableField("I_MILE")
    private Double iMile;
    /**
     * 状态
     */
    @TableField("C_STATUE")
    private String cStatue;
    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String sRemark;
    /**
     * 所属单位编码
     */
    @TableField("S_DEPTCODE")
    private String sDeptcode;
    /**
     * 传输状态（0：未传输，1：已传输）
     */
    @TableField("S_TRANSPORTSTATUS")
    private String sTransportstatus;
    /**
     * 传输时间
     */
    @TableField("S_TRANSPORTDATE")
    private Timestamp sTransportdate;
    /**
     * 项目扩展情况：0 辆序 1 部件
     */
    @TableField("S_ARRANGETYPE")
    private String sArrangetype;


    public String getsTaskitemid() {
        return sTaskitemid;
    }

    public void setsTaskitemid(String sTaskitemid) {
        this.sTaskitemid = sTaskitemid;
    }

    public String getsTaskid() {
        return sTaskid;
    }

    public void setsTaskid(String sTaskid) {
        this.sTaskid = sTaskid;
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

    public String getsDayplanid() {
        return sDayplanid;
    }

    public void setsDayplanid(String sDayplanid) {
        this.sDayplanid = sDayplanid;
    }

    public String getsPartid() {
        return sPartid;
    }

    public void setsPartid(String sPartid) {
        this.sPartid = sPartid;
    }

    public String getdCreatdate() {
        return dCreatdate;
    }

    public void setdCreatdate(String dCreatdate) {
        this.dCreatdate = dCreatdate;
    }

    public String getsItemcode() {
        return sItemcode;
    }

    public void setsItemcode(String sItemcode) {
        this.sItemcode = sItemcode;
    }

    public String getsItemname() {
        return sItemname;
    }

    public void setsItemname(String sItemname) {
        this.sItemname = sItemname;
    }

    public String getsItemtype() {
        return sItemtype;
    }

    public void setsItemtype(String sItemtype) {
        this.sItemtype = sItemtype;
    }

    public String getsCar() {
        return sCar;
    }

    public void setsCar(String sCar) {
        this.sCar = sCar;
    }

    public String getsPositioncode() {
        return sPositioncode;
    }

    public void setsPositioncode(String sPositioncode) {
        this.sPositioncode = sPositioncode;
    }

    public String getsPositionname() {
        return sPositionname;
    }

    public void setsPositionname(String sPositionname) {
        this.sPositionname = sPositionname;
    }

    public String getsLocationcode() {
        return sLocationcode;
    }

    public void setsLocationcode(String sLocationcode) {
        this.sLocationcode = sLocationcode;
    }

    public String getsLocationname() {
        return sLocationname;
    }

    public void setsLocationname(String sLocationname) {
        this.sLocationname = sLocationname;
    }

    public String getsKeyparttypeid() {
        return sKeyparttypeid;
    }

    public void setsKeyparttypeid(String sKeyparttypeid) {
        this.sKeyparttypeid = sKeyparttypeid;
    }

    public String getsModelid() {
        return sModelid;
    }

    public void setsModelid(String sModelid) {
        this.sModelid = sModelid;
    }

    public String getsModelname() {
        return sModelname;
    }

    public void setsModelname(String sModelname) {
        this.sModelname = sModelname;
    }

    public String getdBegindate() {
        return dBegindate;
    }

    public void setdBegindate(String dBegindate) {
        this.dBegindate = dBegindate;
    }

    public String getdEnddate() {
        return dEnddate;
    }

    public void setdEnddate(String dEnddate) {
        this.dEnddate = dEnddate;
    }

    public Double getiMile() {
        return iMile;
    }

    public void setiMile(Double iMile) {
        this.iMile = iMile;
    }

    public String getcStatue() {
        return cStatue;
    }

    public void setcStatue(String cStatue) {
        this.cStatue = cStatue;
    }

    public String getsRemark() {
        return sRemark;
    }

    public void setsRemark(String sRemark) {
        this.sRemark = sRemark;
    }

    public String getsDeptcode() {
        return sDeptcode;
    }

    public void setsDeptcode(String sDeptcode) {
        this.sDeptcode = sDeptcode;
    }

    public String getsTransportstatus() {
        return sTransportstatus;
    }

    public void setsTransportstatus(String sTransportstatus) {
        this.sTransportstatus = sTransportstatus;
    }

    public Timestamp getsTransportdate() {
        return sTransportdate;
    }

    public void setsTransportdate(Timestamp sTransportdate) {
        this.sTransportdate = sTransportdate;
    }

    public String getsArrangetype() {
        return sArrangetype;
    }

    public void setsArrangetype(String sArrangetype) {
        this.sArrangetype = sArrangetype;
    }

    @Override
    protected Serializable pkVal() {
        return this.sTaskitemid;
    }

    @Override
    public String toString() {
        return "PzMRepairtaskItem{" +
        "sTaskitemid=" + sTaskitemid +
        ", sTaskid=" + sTaskid +
        ", sTrainsetid=" + sTrainsetid +
        ", sTrainsetname=" + sTrainsetname +
        ", sDayplanid=" + sDayplanid +
        ", sPartid=" + sPartid +
        ", dCreatdate=" + dCreatdate +
        ", sItemcode=" + sItemcode +
        ", sItemname=" + sItemname +
        ", sItemtype=" + sItemtype +
        ", sCar=" + sCar +
        ", sPositioncode=" + sPositioncode +
        ", sPositionname=" + sPositionname +
        ", sLocationcode=" + sLocationcode +
        ", sLocationname=" + sLocationname +
        ", sKeyparttypeid=" + sKeyparttypeid +
        ", sModelid=" + sModelid +
        ", sModelname=" + sModelname +
        ", dBegindate=" + dBegindate +
        ", dEnddate=" + dEnddate +
        ", iMile=" + iMile +
        ", cStatue=" + cStatue +
        ", sRemark=" + sRemark +
        ", sDeptcode=" + sDeptcode +
        ", sTransportstatus=" + sTransportstatus +
        ", sTransportdate=" + sTransportdate +
        ", sArrangetype=" + sArrangetype +
        "}";
    }
}
