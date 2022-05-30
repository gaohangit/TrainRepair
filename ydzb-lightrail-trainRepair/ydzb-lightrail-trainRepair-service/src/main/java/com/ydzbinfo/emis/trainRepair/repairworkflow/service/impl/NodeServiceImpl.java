package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Node;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 节点表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Service
public class NodeServiceImpl extends ServiceImpl<NodeMapper, Node> implements INodeService {
    @Resource
    NodeMapper nodeMapper;

    @Override
    public List<NodeWithExtraInfo> getNodeWithExtraInfoList(String flowId) {
        return nodeMapper.getNodeWithExtraInfoList(flowId);
    }

    @Override
    public void delNodeByFlowId(String flowId) {
        MybatisPlusUtils.delete(
            nodeMapper,
            eqParam(Node::getFlowId, flowId)
        );
    }

    @Override
    public void addNode(Node node) {
        nodeMapper.insert(node);
    }

    @Override
    public Node getNodeInfoById(String nodeId) {
        Node node = new Node();
        node.setId(nodeId);
        return nodeMapper.selectOne(node);
    }

    @Override
    public void updNode(Node node) {
        nodeMapper.updateById(node);
    }

    @Override
    public NodeWithExtraInfo getNodeWithExtraInfoByNodeId(String NodeId) {
        return nodeMapper.getNodeWithExtraInfoByNodeId(NodeId);
    }
}
