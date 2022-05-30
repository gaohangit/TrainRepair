package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Description: rfid标准关系实体
 *
 * Author: 吴跃常
 * Create Date Time: 2021/7/28 14:33
 * Update Date Time: 2021/7/28 14:33
 *
 */
@Data
@TableName("XZY_C_RFIDCAR_CRITERTION")
public class XzyCRfidcarCritertion {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 标准id
     */
    @TableField("S_CRITERTIONID")
    private String critertionId;

    /**
     * 作业位置CODE
     */
    @TableField("S_REPAIRPLACECODE")
    private String repairPlaceCode;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

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
}
