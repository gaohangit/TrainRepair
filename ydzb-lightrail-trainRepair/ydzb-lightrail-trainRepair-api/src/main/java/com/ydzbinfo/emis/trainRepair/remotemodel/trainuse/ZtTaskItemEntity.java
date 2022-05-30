package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import com.ydzbinfo.emis.trainRepair.common.querymodel.ITaskItemEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 任务实体
 * </p>
 *
 * @author shenml
 * @since 2020-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ZtTaskItemEntity implements ITaskItemEntityBase {

    /**
     * 任务ID
     */
    private String taskItemId;

    /**
     * 任务包ID
     */
    private String taskId;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 班次
     */
    private String dayplanId;

    /**
     * 配件ID
     */
    private String partId;

    /**
     * 生成时间
     */
    private Date createDate;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目类型
     */
    private String itemType;

    /**
     * 项目扩展情况：0辆序，1部件
     */
    private String arrangeType;

    /**
     * 辆序
     */
    private String carNo;

    /**
     * 功能位编码
     */
    private String positionCode;

    /**
     * 功能位名称
     */
    private String positionName;

    /**
     * 位置编码
     */
    private String locationCode;

    /**
     * 位置名称
     */
    private String locationName;

    /**
     * 实体件编码
     */
    private String modelCode;

    /**
     * 实体件名称
     */
    private String modelName;

    /**
     * 配件类型编码
     */
    private String keyPartTypeId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String status;

    /**
     * 所属单位编码
     */
    private String depotCode;

    /**
     * 检修开始时间
     */
    private Date repairBeginDate;

    /**
     * 检修结束时间
     */
    private Date repairEndDate;

    /**
     * 走形公里数
     */
    private Double mile;

}
