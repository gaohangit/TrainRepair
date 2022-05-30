package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 节点记录表
 * </p>
 *
 * @author gaohan
 * @since 2021-05-24
 */
@TableName("XZY_M_NODERECORD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeRecord implements Serializable {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 流程运行表主键
     */
    @TableField("S_FLOWRUNID")
    private String flowRunId;

    /**
     * 节点主键
     */
    @TableField("S_NODEID")
    private String nodeId;

    /**
     * 记录方式(PERSION 人员操作)
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 操作类型
     */
    @TableField("S_OPERATIONTYPE")
    private String operationType;

    /**
     * 记录时间
     */
    @TableField("D_RECORDTIME")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date recordTime;

    /**
     * 记录者id
     */
    @TableField("S_WORKERID")
    private String workerId;

    /**
     * 记录者名称
     */
    @TableField("S_WORKERNAME")
    private String workerName;

    /**
     * 记录角色id
     */
    @TableField("S_ROLEID")
    private String roleId;

    /**
     * 记录角色名称
     */
    @TableField("S_ROLENAME")
    private String roleName;

    /**
     * 记录角色类型
     */
    @TableField("S_ROLETYPE")
    private String roleType;

    /**
     * 记录者班组code
     */
    @TableField("S_TEAMCODE")
    private String teamCode;

    /**
     * 记录者班组名称
     */
    @TableField("S_TEAMNAME")
    private String teamName;

    /**
     * 是否存在额外信息
     */
    @TableField("C_EXTRAINFOEXIST")
    private String extraInfoExist;
}
