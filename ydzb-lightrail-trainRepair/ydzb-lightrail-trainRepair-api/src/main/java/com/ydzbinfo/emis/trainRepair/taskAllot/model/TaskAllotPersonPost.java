package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-05
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

    /**
     * 派工唯一编码
     */
    @TableField("S_PUBLISHCODE")
    private String publishCode;

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

    public String getPublishCode() {
        return publishCode;
    }

    public void setPublishCode(String publishCode) {
        this.publishCode = publishCode;
    }

    @Override
    public String toString() {
        return "TaskAllotPersonPost{" +
            "id='" + id + '\'' +
            ", taskAllotPersonId='" + taskAllotPersonId + '\'' +
            ", postId='" + postId + '\'' +
            ", postName='" + postName + '\'' +
            ", publishCode='" + publishCode + '\'' +
            '}';
    }
}
