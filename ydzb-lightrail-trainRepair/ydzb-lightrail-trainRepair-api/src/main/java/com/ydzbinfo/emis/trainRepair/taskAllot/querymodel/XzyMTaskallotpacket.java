package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-11
 */
public class XzyMTaskallotpacket extends Model<XzyMTaskallotpacket> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String taskAllotPacketId;
    /**
     * 作业包CODE
     */
    private String packetCode;
    /**
     * 作业包NAME
     */
    private String packetName;
    /**
     * 作业包类型
     */
    private String packetType;
    /**
     * 任务包ID
     */
    private String staskId;
    /**
     * 辆序部件集合
     */
    private List<XzyMTaskcarpart> taskcarpartList = new ArrayList<>();

    /**
     * 车组id(后台用）
     */
    private String trainsetId;

    /**
     * 修程
     */
    private String mainCyc;
    /**
     * 派工项目
     */
    private List<XzyMTaskallotItem> taskallotItemList;

    public List<XzyMTaskallotItem> getTaskallotItemList() {
        return taskallotItemList;
    }

    public void setTaskallotItemList(List<XzyMTaskallotItem> taskallotItemList) {
        this.taskallotItemList = taskallotItemList;
    }

    public String getTaskAllotPacketId() {
        return taskAllotPacketId;
    }

    public void setTaskAllotPacketId(String taskAllotPacketId) {
        this.taskAllotPacketId = taskAllotPacketId;
    }

    public String getPacketCode() {
        return packetCode;
    }

    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public String getPacketName() {
        return packetName;
    }

    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public String getStaskId() {
        return staskId;
    }

    public void setStaskId(String staskId) {
        this.staskId = staskId;
    }

    public List<XzyMTaskcarpart> getTaskcarpartList() {
        return taskcarpartList;
    }

    public void setTaskcarpartList(List<XzyMTaskcarpart> taskcarpartList) {
        this.taskcarpartList = taskcarpartList;
    }

    public String getTrainsetId() {
        return trainsetId;
    }

    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public String getMainCyc() {
        return mainCyc;
    }

    public void setMainCyc(String mainCyc) {
        this.mainCyc = mainCyc;
    }

    @Override
    public String toString() {
        return "XzyMTaskallotpacket{" +
            "taskAllotPacketId='" + taskAllotPacketId + '\'' +
            ", packetCode='" + packetCode + '\'' +
            ", packetName='" + packetName + '\'' +
            ", packetType='" + packetType + '\'' +
            ", staskId='" + staskId + '\'' +
            ", taskcarpartList=" + taskcarpartList +
            ", trainsetId='" + trainsetId + '\'' +
            ", mainCyc='" + mainCyc + '\'' +
            '}';
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
