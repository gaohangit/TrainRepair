package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/22 17:56
 **/
public class GroupModel {
    //小组id
    private String   branchCode;
    //小组
    private String  branchName;
    //排序
    private String sort;



    //部门code
    private String deptCode;
    //部门名称
    private String deptName;
    //单位编码
    private String unitCode;
    //状态
    private  String flag;
    //人员
    List<PerSonNelModel> perSonNelModels;



    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
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

    public List<PerSonNelModel> getPerSonNelModels() {
        return perSonNelModels;
    }

    public void setPerSonNelModels(List<PerSonNelModel> perSonNelModels) {
        this.perSonNelModels = perSonNelModels;
    }
}
