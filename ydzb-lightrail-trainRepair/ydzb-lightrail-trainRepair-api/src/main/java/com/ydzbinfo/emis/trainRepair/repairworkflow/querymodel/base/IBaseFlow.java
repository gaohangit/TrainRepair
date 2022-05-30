package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base;

/**
 * @author 张天可
 * @since 2021/7/7
 */
public interface IBaseFlow {
    String getId();

    String getName();

    String getFlowTypeCode();

    java.util.Date getCreateTime();

    String getCreateWorkerId();

    String getCreateWorkerName();

    String getUnitCode();

    java.util.Date getDeleteTime();

    String getDeleteWorkerId();

    String getDeleteWorkerName();

    void setId(String id);

    void setName(String name);

    void setFlowTypeCode(String flowTypeCode);

    void setCreateTime(java.util.Date createTime);

    void setCreateWorkerId(String createWorkerId);

    void setCreateWorkerName(String createWorkerName);

    void setUnitCode(String unitCode);

    void setDeleteTime(java.util.Date deleteTime);

    void setDeleteWorkerId(String deleteWorkerId);

    void setDeleteWorkerName(String deleteWorkerName);
}
