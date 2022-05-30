package com.ydzbinfo.emis.trainRepair.taskAllot.model;

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
 * @since 2021-06-19
 */
@TableName("XZY_M_TASKALLOTPERSONPOSTHIS")
@Data
public class TaskAllotPersonPostHis implements Serializable {

    @TableId("S_ID")
    private String id;

    @TableField("S_TASKALLOTPERSONID")
    private String taskAllotPersonId;

    @TableField("S_POSTID")
    private String postId;

    @TableField("S_POSTNAME")
    private String postName;
}
