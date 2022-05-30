package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

/**
 * @author: fengshuai
 * @Date: 2021/3/31
 * @Description: 派工人员岗位（中台服务）
 */
public class TaskAllotPersonPostEntity {

    /**
     * 主键
     */
    private String sId;

    /**
     * 岗位
     */
    private String postId;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 派工人员ID
     */
    private String taskAllotPersonId;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
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

    public String getTaskAllotPersonId() {
        return taskAllotPersonId;
    }

    public void setTaskAllotPersonId(String taskAllotPersonId) {
        this.taskAllotPersonId = taskAllotPersonId;
    }

    @Override
    public String toString() {
        return "TaskAllotPersonPostEntity{" +
                "sId='" + sId + '\'' +
                ", postId='" + postId + '\'' +
                ", postName='" + postName + '\'' +
                ", taskAllotPersonId='" + taskAllotPersonId + '\'' +
                '}';
    }
}
