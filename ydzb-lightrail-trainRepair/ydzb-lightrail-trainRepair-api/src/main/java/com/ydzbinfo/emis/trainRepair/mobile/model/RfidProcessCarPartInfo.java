package com.ydzbinfo.emis.trainRepair.mobile.model;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Description:     作业过程开始保存标签参数
 * Author: wuyuechang
 * Create Date Time: 2021/5/6 10:49
 * Update Date Time: 2021/5/6 10:49
 *
 * @see
 */
@Data
public class RfidProcessCarPartInfo {

    /**
     * 项目状态（1-开始，2-暂停，3-继续，4-结束）
     */
    private String itemTimeState;

    /**
     * 项目状态（1-开始，2-暂停，3-继续，4-结束）
     */
    private String timeTag;

    /**
     * 日计划ID
     */
    private String dayplanId;
    /**
     * 车组名称
     */
    private String trainsetName;
    /**
     * 车组ID
     */
    private String trainsetId;
    /**
     * 运用所CODE
     */
    private String unitCode;
    /**
     * 运用所名称
     */
    private String unitName;
    /**
     * 项目CODE
     */
    private String itemCode;
    /**
     * 项目NAME
     */
    private String itemName;
    /**
     * 检修类型（项目类型）
     */
    private String repairType;
    /**
     * 项目作业类型   1--部件  0--辆序
     */
    private String arrageType;
    /**
     * 数据来源
     */
    private String dataSource;
    /**
     * 部件类型名称
     */
    private String partType;
    /**
     * 部件位置
     */
    private String partPosition;
    /**
     * 备注
     */
    private String remark;
    /**
     * 检修方式  0--人工检查   1--机器人     2--机检
     */
    private String repairMode;
    /**
     * 检修时间
     */
    private Date repairTime;
    /**
     * 部件名称
     */
    private String partName;
    /**
     * 记录时间
     */
    private Date recordTime;
    /**
     * 记录人姓名
     */
    private String recorderName;
    /**
     * 记录人编码
     */
    private String recorderCode;
    /**
     * 派工包表主键
     */
    private String taskAllotPacketId;
    /**
     * 派工部门表主键
     */
    private String taskAllotDeptId;
    /**
     * 辆序
     */
    private String carNo;
    /**
     * 车型
     */
    private String trainsetType;
    /**
     * 计划任务ID
     */
    private String taskItemId;

    /**
     * 派工包Name
     */
    private String packetName;

    /**
     * 派工包Code
     */
    private String packetCode;

    /**
     * 派工包Type
     */
    private String packetType;

    /**
     * 车组位置动态历史表id
     */
    private String trainsetPostionId;

    /**
     * 项目唯一标识
     */
    private String itemPublcished;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编号
     */
    private String deptCode;

    /**
     * 作业人员姓名
     */
    private String workerName;

    /**
     * 作业人员id
     */
    private String stuffId;

    /**
     *作业人员类型
     */
    private String workerType;

    /**
     * 股道编号
     */
    private String trackCode;

    /**
     * 股道名称
     */
    private String trackName;

    /**
     * 作业标准主键，表XZY_C_WORKCRITERTION
     */
    private String critertionId;

    /**
     * 作业位置名称（列位名称）
     */
    private String repairPlaceName;

    /**
     * 作业位置code（列位code）
     */
    private String repairPlaceCode;

    /**
     * 定位地点
     */
    private String location;

    /**
     * 定位设备TID
     */
    private String tId;

    /**
     * 定位开始时间
     */
    private String starttime;

    /**
     * 定位结束时间
     */
    private String endTime;

    /**
     * 排序id
     */
    private String sortId;

    /**
     * 派工包
     */
    private List<XzyMTaskallotpacket> packetList;

    /**
     * 项目code集合
     */
    private List<String> itemCodes;
}
