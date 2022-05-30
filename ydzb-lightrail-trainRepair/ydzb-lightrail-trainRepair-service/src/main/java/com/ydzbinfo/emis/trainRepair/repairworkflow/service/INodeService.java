package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Node;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeWithExtraInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 节点表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface INodeService extends IService<Node> {
    /**
     * 查询节点信息
     */
    List<NodeWithExtraInfo> getNodeWithExtraInfoList(String flowId);

    /**
     * 删除节点信息
     */
    void  delNodeByFlowId(String flowId);
    /**
     * 新增节点
     */
    void addNode(Node node);

    Node getNodeInfoById(String nodeId);

    void updNode(Node node);


    /**
     * 根据节点查到节点配置
     * @param NodeId
     * @return
     */
    NodeWithExtraInfo getNodeWithExtraInfoByNodeId(@Param("NodeId") String NodeId);

}
