package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeVectorMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVector;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeVectorService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 节点流向表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Service
public class NodeVectorServiceImpl extends ServiceImpl<NodeVectorMapper, NodeVector> implements INodeVectorService {
    @Resource
    NodeVectorMapper nodeVectorMapper;

    @Override
    public List<NodeVectorWithExtraInfo> getNodeVectorWithInfoList(String flowId) {
        return nodeVectorMapper.getNodeVectorWithInfoList(flowId);
    }

    @Override
    public void delNodeVector(String flowId) {
        MybatisPlusUtils.delete(
            nodeVectorMapper,
            eqParam(NodeVector::getFlowId, flowId)
        );
    }

    @Override
    public void addNodeVector(NodeVector nodeVector) {
        nodeVectorMapper.insert(nodeVector);
    }

    @Override
    public List<NodeVector> getNodeVector(String flowId) {
        return MybatisPlusUtils.selectList(
            nodeVectorMapper,
            eqParam(NodeVector::getFlowId, flowId)
        );
    }
}
