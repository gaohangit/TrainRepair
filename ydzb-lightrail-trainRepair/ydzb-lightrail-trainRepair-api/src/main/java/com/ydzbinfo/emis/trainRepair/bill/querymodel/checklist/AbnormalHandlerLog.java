package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 异常派工回填记录表
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ZY_M_ABNORMALHANDLERLOG")
public class AbnormalHandlerLog implements Serializable {


    /**
     * 异常派工回填记录表主键ID
     */
    @TableId("S_ABNORMALHANDLERLOGID")
    private String abnormalHandlerLogId;

    /**
     * 异常回填时的记录单ID
     */
    @TableField("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

    /**
     * 修程（1：一级修；2：二级修）
     */
    @TableField("S_MAINCYC")
    private String mainCyc;

    /**
     * 作业包编码
     */
    @TableField("S_PACKETCODE")
    private String packetCode;

    /**
     * 作业包名称
     */
    @TableField("S_PACKETNAME")
    private String packetName;

    /**
     * 项目编码
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目名称
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 操作员工编码
     */
    @TableField("S_STAFFCODE")
    private String staffCode;

    /**
     * 操作员工名称
     */
    @TableField("S_STAFFNAME")
    private String staffName;

    /**
     * 操作时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 操作类型（0：工长派工；1：地勤机械师回填记录单；2：工长记录单签字）
     */
    @TableField("C_HANDLERTYPE")
    private String handlerType;

    /**
     * 操作设备IP地址
     */
    @TableField("S_IPADDRESS")
    private String ipAddress;

    /**
     * 操作设备名称
     */
    @TableField("S_HOSTNAME")
    private String hostname;

    /**
     * 备注信息
     */
    @TableField("S_REMARKS")
    private String remarks;

    /**
     * 通报计算程序使用标志：0-未使用，1-已使用
     */
    @TableField("C_USEFLAG")
    private String useFlag;


}
