package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.ydzbinfo.emis.trainRepair.common.querymodel.ITaskItemEntityBase;
import lombok.Data;

import java.util.List;

/**
 * @author: fengshuai
 * @Date: 2021/3/31
 * @Description: 派工任务实体（中台服务）
 */
@Data
public class TaskAllotItemEntity implements ITaskItemEntityBase {
    /**
     * 备注
     */
    private String remark;

    /**
     * 部件类型
     */
    private String partType;

    /**
     * 部件名称
     */
    private String partName;

    /**
     * 部件id
     */
    private String partId;

    /**
     * 部件位置编码
     */
    private String partPosition;

    /**
     * 位置编码
     */
    private String locationCode;

    /**
     * 位置名称
     */
    private String locationName;

    /**
     * 计划项目ID
     */
    private String taskItemId;

    /**
     * 检修方式
     */
    private String repairMode;

    /**
     * 辆序
     */
    private String carNo;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 项目类型
     */
    private String itemType;

    /**
     * 项目唯一编码
     */
    private String publishCode;

    /**
     * 项目作业类型
     */
    private String arrageType;

    /**
     * 主键
     */
    private String Id;

    /**
     *作业项目编码
     */
    private String itemCode;

    /**
     *作业项目名称
     */
    private String itemName;

    /**
     *派工人员集合
     */
    private List<TaskAllotPersonEntity> taskAllotPersonEntityList;

    /**
     * 派工包主键
     */
    private String taskAllotPacketId;

    /**
     * 数据来源
     */
    private String dataSource;
}
