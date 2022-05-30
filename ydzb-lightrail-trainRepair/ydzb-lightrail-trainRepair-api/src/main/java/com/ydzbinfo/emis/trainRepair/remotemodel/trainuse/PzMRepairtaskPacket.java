package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 任务包表
 * </p>
 *
 * @author tzt
 * @since 2020-09-02
 */
@TableName("PZ_M_REPAIRTASK_PACKET")
public class PzMRepairtaskPacket extends Model<PzMRepairtaskPacket> {

    private static final long serialVersionUID = 1L;
    
//    private PzMRepairtaskItem pzMRepairtaskItem; //校验用，勿动

    /**
     * 主键/任务包ID
     */
    @TableId("S_TASKID")
    private String sTaskid;
    /**
     * 修程编码
     */
    @TableField("S_TASKREPAIRCODE")
    private String sTaskrepaircode;
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
     * 包编码
     */
    @TableField("S_PACKETCODE")
    private String sPacketcode;
    /**
     * 包名称
     */
    @TableField("S_PACKETNAME")
    private String sPacketname;
    /**
     * 作业包类型编码
     */
    @TableField("S_PACKETTYPECODE")
    private String sPackettypecode;
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
     * 计划开始时间
     */
    @TableField("D_BEGINDATE")
    private String dBegindate;
    /**
     * 计划结束时间
     */
    @TableField("D_ENDDATE")
    private String dEnddate;
    /**
     * 班次
     */
    @TableField("S_DAYPLANID")
    private String sDayplanid;
    /**
     * 检修班组编码
     */
    @TableField("S_REPAIRUNITCODE")
    private String sRepairunitcode;
    /**
     * 检修班组名称
     */
    @TableField("S_REPAIRUNITNAME")
    private String sRepairunitname;
    /**
     * 检修股道编码
     */
    @TableField("S_TRACKCODE")
    private String sTrackcode;
    /**
     * 检修股道名称
     */
    @TableField("S_TRACKNAME")
    private String sTrackname;
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

    private List<PzMRepairtaskItem> pzMRepairtaskItem;

    public List<PzMRepairtaskItem> getPzMRepairtaskItem() {
        return pzMRepairtaskItem;
    }

    public void setPzMRepairtaskItem(List<PzMRepairtaskItem> pzMRepairtaskItem) {
        this.pzMRepairtaskItem = pzMRepairtaskItem;
    }

    public String getsTaskid() {
        return sTaskid;
    }

    public void setsTaskid(String sTaskid) {
        this.sTaskid = sTaskid;
    }

    public String getsTaskrepaircode() {
        return sTaskrepaircode;
    }

    public void setsTaskrepaircode(String sTaskrepaircode) {
        this.sTaskrepaircode = sTaskrepaircode;
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

    public String getsPacketcode() {
        return sPacketcode;
    }

    public void setsPacketcode(String sPacketcode) {
        this.sPacketcode = sPacketcode;
    }

    public String getsPacketname() {
        return sPacketname;
    }

    public void setsPacketname(String sPacketname) {
        this.sPacketname = sPacketname;
    }

    public String getsPackettypecode() {
        return sPackettypecode;
    }

    public void setsPackettypecode(String sPackettypecode) {
        this.sPackettypecode = sPackettypecode;
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

    public String getsDayplanid() {
        return sDayplanid;
    }

    public void setsDayplanid(String sDayplanid) {
        this.sDayplanid = sDayplanid;
    }

    public String getsRepairunitcode() {
        return sRepairunitcode;
    }

    public void setsRepairunitcode(String sRepairunitcode) {
        this.sRepairunitcode = sRepairunitcode;
    }

    public String getsRepairunitname() {
        return sRepairunitname;
    }

    public void setsRepairunitname(String sRepairunitname) {
        this.sRepairunitname = sRepairunitname;
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

    @Override
    protected Serializable pkVal() {
        return this.sTaskid;
    }

    @Override
    public String toString() {
        return "PzMRepairtaskPacket{" +
        "sTaskid=" + sTaskid +
        ", sTaskrepaircode=" + sTaskrepaircode +
        ", sTrainsetid=" + sTrainsetid +
        ", sTrainsetname=" + sTrainsetname +
        ", sPacketcode=" + sPacketcode +
        ", sPacketname=" + sPacketname +
        ", sPackettypecode=" + sPackettypecode +
        ", sPartid=" + sPartid +
        ", dCreatdate=" + dCreatdate +
        ", dBegindate=" + dBegindate +
        ", dEnddate=" + dEnddate +
        ", sDayplanid=" + sDayplanid +
        ", sRepairunitcode=" + sRepairunitcode +
        ", sRepairunitname=" + sRepairunitname +
        ", sTrackcode=" + sTrackcode +
        ", sTrackname=" + sTrackname +
        ", cStatue=" + cStatue +
        ", sRemark=" + sRemark +
        ", sDeptcode=" + sDeptcode +
        ", sTransportstatus=" + sTransportstatus +
        ", sTransportdate=" + sTransportdate +
        "}";
    }
}
