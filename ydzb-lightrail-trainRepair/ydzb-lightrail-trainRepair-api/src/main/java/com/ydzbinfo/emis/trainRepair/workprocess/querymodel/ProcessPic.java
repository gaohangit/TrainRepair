package com.ydzbinfo.emis.trainRepair.workprocess.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
@TableName("XZY_M_PROCESSPIC")
@Data
public class ProcessPic implements Serializable {


    /**
     * 主键
     */
    @TableId("S_PICID")
    private String picId;

    /**
     * 图片地址
     */
    @TableField("S_PICADDRESS")
    private String picAddress;

    /**
     * 图片名称
     */
    @TableField("S_PICNAME")
    private String picName;

    /**
     * 图片类型  1-- 图片  2-- 视频
     */
    @TableField("S_PICTYPE")
    private String picType;

    /**
     * 作业类型  1-- 1级修   2 -- 2级修  3-- 出所联检 4 -- 协同
     */
    @TableField("S_WORKTYPE")
    private String workType;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayplanId;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 部件位置
     */
    @TableField("S_PARTPOSITION")
    private String partPosition;

    /**
     * 作业人员ID
     */
    @TableField("S_WORKERID")
    private String workerId;

    /**
     *
     * 作业辆序
     */
    @TableField("S_CARNO")
    private String carNo;

    /**
     * 排序ID
     */
    @TableField("S_SORTID")
    private String sortId;

    /**
     * 部件名称
     */
    @TableField("S_PARTNAME")
    private String partName;

    /**
     * 部件类型名称
     */
    @TableField("S_PARTTYPE")
    private String partType;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    private String itemName;
}
