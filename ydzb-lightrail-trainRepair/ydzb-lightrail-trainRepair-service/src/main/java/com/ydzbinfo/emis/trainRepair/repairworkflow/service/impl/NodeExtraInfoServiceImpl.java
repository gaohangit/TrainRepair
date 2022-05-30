package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeExtraInfoMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeExtraInfoService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 节点额外信息表	如：是否可跳过、节点激活聚合条件、预警时间信息、动态节点额外条件、节点路由标识（主、次等等，在程序内使用枚举值定义，并由此在程序中定义并行节点的基础路由方法）等 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Service
public class NodeExtraInfoServiceImpl extends ServiceImpl<NodeExtraInfoMapper, NodeExtraInfo> implements INodeExtraInfoService {
    @Resource
    NodeExtraInfoMapper nodeExtraInfoMapper;

    @Override
    public void delNodeExtraInfoByNodeId(List<NodeWithExtraInfo> list) {
        for (NodeWithExtraInfo nodeWithExtraInfo : list) {
            MybatisPlusUtils.delete(
                nodeExtraInfoMapper,
                eqParam(NodeExtraInfo::getNodeId, nodeWithExtraInfo.getId())
            );
        }
    }

    @Override
    public void addNodeExtraInfo(NodeExtraInfo nodeExtraInfo) {
        nodeExtraInfoMapper.insert(nodeExtraInfo);
    }
}
