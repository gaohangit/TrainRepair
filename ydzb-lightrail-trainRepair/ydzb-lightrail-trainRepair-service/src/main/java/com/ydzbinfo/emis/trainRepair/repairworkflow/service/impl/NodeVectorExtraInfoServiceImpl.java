package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeVectorExtraInfoMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVectorExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeVectorExtraInfoService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 节点流向额外信息表	 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Service
public class NodeVectorExtraInfoServiceImpl extends ServiceImpl<NodeVectorExtraInfoMapper, NodeVectorExtraInfo> implements INodeVectorExtraInfoService {
    @Resource
    NodeVectorExtraInfoMapper nodeVectorExtraInfoMapper;

    @Override
    public void delNodeVectorExtraInfoById(List<NodeVectorWithExtraInfo> nodeVectorWithExtraInfos) {
        for (NodeVectorWithExtraInfo nodeVectorWithExtraInfo : nodeVectorWithExtraInfos) {
            MybatisPlusUtils.delete(
                nodeVectorExtraInfoMapper,
                eqParam(NodeVectorExtraInfo::getNodeVectorId, nodeVectorWithExtraInfo.getId())
            );
        }
    }

    @Override
    public void addNodeVectorExtraInfo(NodeVectorExtraInfo nodeVectorExtraInfo) {
        nodeVectorExtraInfoMapper.insert(nodeVectorExtraInfo);
    }
}
