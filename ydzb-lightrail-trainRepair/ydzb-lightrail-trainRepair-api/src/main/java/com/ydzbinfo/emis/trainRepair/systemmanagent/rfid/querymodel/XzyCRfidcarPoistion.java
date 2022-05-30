package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Description: rfid位置实体
 *
 * Author: 吴跃常
 * Create Date Time: 2021/7/28 14:33
 * Update Date Time: 2021/7/28 14:33
 *
 */
@Data
@TableName("XZY_C_RFIDCAR_POISTION")
public class XzyCRfidcarPoistion {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("S_RULEID")
    private String ruleId;

    /**
     * 编组数量
     */
    @TableField("S_CARCOUNT")
    private Integer carCount;

    /**
     * 车头在1列位时，辆序号
     */
    @TableField("S_HEADDIRECTIONCARNO")
    private String headDirectionCarNo;

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

    /**
     * TID
     */
    @TableField("S_TID")
    private String tId;

    /**
     * 车尾在1列位时，辆序号
     */
    @TableField("S_TAILDIRECTIONCARNO")
    private String tailDirectionCarNo;
}
