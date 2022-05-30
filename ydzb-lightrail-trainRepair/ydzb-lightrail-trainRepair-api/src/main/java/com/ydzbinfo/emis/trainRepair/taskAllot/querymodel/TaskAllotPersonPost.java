package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-29
 */
@TableName("XZY_M_TASKALLOTPERSONPOST")
public class TaskAllotPersonPost implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 派工人员ID
     */
    @TableField("S_TASKALLOTPERSONID")
    private String taskAllotPersonId;

    /**
     * 岗位ID
     */
    @TableField("S_POSTID")
    private String postId;

    /**
     * 岗位名称
     */
    @TableField("S_POSTNAME")
    private String postName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTaskAllotPersonId() {
        return taskAllotPersonId;
    }

    public void setTaskAllotPersonId(String taskAllotPersonId) {
        this.taskAllotPersonId = taskAllotPersonId;
    }
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    @Override
    public String toString() {
        return "TaskAllotPersonPost{" +
            "id=" + id +
            ", taskAllotPersonId=" + taskAllotPersonId +
            ", postId=" + postId +
            ", postName=" + postName +
        "}";
    }
}
