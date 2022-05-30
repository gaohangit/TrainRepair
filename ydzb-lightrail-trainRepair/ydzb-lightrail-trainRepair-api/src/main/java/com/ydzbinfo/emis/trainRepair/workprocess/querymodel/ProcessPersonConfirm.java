package com.ydzbinfo.emis.trainRepair.workprocess.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-21
 */
@Data
@TableName("XZY_M_PROCESSPERSONCONFIRM")
public class ProcessPersonConfirm implements Serializable {

    /**
     * 主键
     */
    @TableId("S_CONFIRMID")
    private String confirmId;

    /**
     * 作业过程人员表主键
     */
    @TableField("S_PROCESSPERSONID")
    private String processPersonId;

    /**
     * 辆序数量
     */
    @TableField("S_CARNOCOUNT")
    private String carNoCount;
}
