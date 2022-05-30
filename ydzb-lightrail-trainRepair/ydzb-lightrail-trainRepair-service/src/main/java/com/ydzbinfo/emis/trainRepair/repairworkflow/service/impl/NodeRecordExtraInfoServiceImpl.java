package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeRecordExtraInfoMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecordExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeRecordExtraInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 节点记录额外信息表	比如：图片信息、表单信息等 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Service
public class NodeRecordExtraInfoServiceImpl extends ServiceImpl<NodeRecordExtraInfoMapper, NodeRecordExtraInfo> implements INodeRecordExtraInfoService {
    @Autowired
    NodeRecordExtraInfoMapper nodeRecordExtraInfoMapper;

    @Override
    public void addNodeRecordExtraInfo(NodeRecordExtraInfo nodeRecordExtraInfo) {
        nodeRecordExtraInfoMapper.insert(nodeRecordExtraInfo);
    }
}
