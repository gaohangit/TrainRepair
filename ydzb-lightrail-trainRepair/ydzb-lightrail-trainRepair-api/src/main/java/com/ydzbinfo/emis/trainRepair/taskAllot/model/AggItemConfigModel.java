package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfigItem;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-17
 */
@TableName("XZY_C_AGGITEMCONFIG")
public class AggItemConfigModel implements Serializable {


    /**
     * 主键
     */
    @TableId("S_PACKETID")
    private String packetId;

    /**
     * 车型
     */
    @TableField("S_TRAINTYPE")
    private String trainType;

    /**
     * 批次code
     */
    @TableField("S_STAGEID")
    private String stageId;

    /**
     * 数据类型(1:一级修,2:二级修,-1:其他)
     */
    @TableField("S_TASKTYPE")
    private String taskType;

    /**
     * 专业分工CODE
     */
    @TableField("S_AGGPACKETCODE")
    private String aggPacketCode;

    /**
     * 专业分工NAME
     */
    @TableField("S_AGGPACKETNAME")
    private String aggPacketName;

    /**
     * 专业分工适用辆序
     */
    @TableField("S_AGGPACKETCAR")
    private String aggPacketCar;

    /**
     * 排序字段
     */
    @TableField("S_SORT")
    private String sort;

    /**
     * 创建人code
     */
    @TableField("S_CREATEUSERNAME")
    private String createUserName;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 修改人code
     */
    @TableField("S_CREATEUSERCODE")
    private String createUserCode;

    /**
     * 运用所code
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 运用所名字
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 结构有变化时数据逻辑删除,VERSIONID重新生成
     */
    @TableField("S_VERSIONID")
    private String versionId;

    /**
     * 是否删除(1:是,0:否)
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 班组CODE
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 班组名称
     */
    @TableField("S_DEPTNAME")
    private String deptName;

    private List<AggItemConfigItem> aggItemConfigItems;




    private String itemName;
    private String itemCode;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public List<AggItemConfigItem> getAggItemConfigItems() {
        return aggItemConfigItems;
    }

    public void setAggItemConfigItems(List<AggItemConfigItem> aggItemConfigItems) {
        this.aggItemConfigItems = aggItemConfigItems;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }
    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }
    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }
    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    public String getAggPacketCode() {
        return aggPacketCode;
    }

    public void setAggPacketCode(String aggPacketCode) {
        this.aggPacketCode = aggPacketCode;
    }
    public String getAggPacketName() {
        return aggPacketName;
    }

    public void setAggPacketName(String aggPacketName) {
        this.aggPacketName = aggPacketName;
    }
    public String getAggPacketCar() {
        return aggPacketCar;
    }

    public void setAggPacketCar(String aggPacketCar) {
        this.aggPacketCar = aggPacketCar;
    }
    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
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
    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }
    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return "AggItemConfig{" +
                "packetId=" + packetId +
                ", trainType=" + trainType +
                ", stageId=" + stageId +
                ", taskType=" + taskType +
                ", aggPacketCode=" + aggPacketCode +
                ", aggPacketName=" + aggPacketName +
                ", aggPacketCar=" + aggPacketCar +
                ", sort=" + sort +
                ", createUserName=" + createUserName +
                ", createTime=" + createTime +
                ", createUserCode=" + createUserCode +
                ", unitCode=" + unitCode +
                ", unitName=" + unitName +
                ", versionId=" + versionId +
                ", flag=" + flag +
                ", deptCode=" + deptCode +
                ", deptName=" + deptName +
                "}";
    }
}
