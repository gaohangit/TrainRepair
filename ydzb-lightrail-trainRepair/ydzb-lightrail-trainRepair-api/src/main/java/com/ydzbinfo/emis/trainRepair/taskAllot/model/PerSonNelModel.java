package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/23 10:43
 * @Modify 冯帅 2021-06-29
 **/
public class PerSonNelModel {

    /**
     * 主键id
     */
    private String  rePairPeRSonAllotId;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 班组编码
     */
    private String   branchCode;

    /**
     * 班组名称
     */
    private String  branchName;

    /**
     * 作业人员编码
     */
    private String workCode;

    /**
     * 作业人员名称
     */
    private String  workName;

    /**
     * 排序
     */
    private String sort;

    /**
     * 是否兼职  1--是  0--否
     */
    private String partTime;

    /**
     * 是否可用  1-是 0-否
     */
    private String flag;

    /**
     * 运用所编码
     */
    private String unitCode;

    /**
     * 岗位集合
     */
    private List<PostModel> postModelList;

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getPartTime() {
        return partTime;
    }

    public void setPartTime(String partTime) {
        this.partTime = partTime;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<PostModel> getPostModelList() {
        return postModelList;
    }

    public void setPostModelList(List<PostModel> postModelList) {
        this.postModelList = postModelList;
    }

    public String getRePairPeRSonAllotId() {
        return rePairPeRSonAllotId;
    }

    public void setRePairPeRSonAllotId(String rePairPeRSonAllotId) {
        this.rePairPeRSonAllotId = rePairPeRSonAllotId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
}
