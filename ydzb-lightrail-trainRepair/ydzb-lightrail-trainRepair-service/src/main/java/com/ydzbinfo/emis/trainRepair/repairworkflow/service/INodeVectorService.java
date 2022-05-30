package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVector;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;

import java.util.List;

/**
 * <p>
 * 节点流向表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface INodeVectorService extends IService<NodeVector> {
    /**
     *节点向量和节点向量额外信息
     */
    List<NodeVectorWithExtraInfo> getNodeVectorWithInfoList(String flowId);

    /**
     * 删除节点向量
     */
    void delNodeVector(String flowId);
    /**
     * 新增节点向量
     */
    void addNodeVector(NodeVector nodeVector);
    /**
     * 节点向量
     */
    List<NodeVector> getNodeVector(String flowId);
}
