package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

/**
 * 区段信息接口
 *
 * @author 张天可
 */
public interface IFlowSection {

    /**
     * 获取区段唯一值
     * @return
     */
    String getId();

    /**
     * 获取区段节点id列表
     * @return
     */
    String[] getNodeIds();
}
