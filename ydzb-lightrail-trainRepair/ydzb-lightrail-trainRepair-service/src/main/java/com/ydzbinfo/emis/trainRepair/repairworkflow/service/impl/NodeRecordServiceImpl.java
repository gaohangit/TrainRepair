package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeRecordExtraInfoMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeRecordMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.NodeRecordInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryNodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecordExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeRecordWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeRecordExtraInfoService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeRecordService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 节点记录表 服务实现类
 * </p>
 *
 * @author 张天可
 * @since 2021-04-02
 */
@Service
public class NodeRecordServiceImpl extends ServiceImpl<NodeRecordMapper, NodeRecord> implements INodeRecordService {
    @Autowired
    NodeRecordMapper nodeRecordMapper;

    @Autowired
    NodeRecordExtraInfoMapper nodeRecordExtraInfoMapper;

    @Autowired
    INodeRecordExtraInfoService nodeRecordExtraInfoService;

    @Override
    public List<NodeRecordInfo> getNodeRecordInfoList(QueryNodeRecord queryNodeRecord) {
        return FlowUtil.parseNodeRecordInfo(nodeRecordMapper.getNodeRecordWithExtraInfo(queryNodeRecord));
    }

    @Transactional
    @Override
    public void insertWithExtraInfo(NodeRecordWithExtraInfo nodeRecordWithExtraInfo) {
        this.insert(nodeRecordWithExtraInfo);
        for (NodeRecordExtraInfo extraInfo : nodeRecordWithExtraInfo.getNodeRecordExtraInfoList()) {
            nodeRecordExtraInfoMapper.insert(extraInfo);
        }
    }

    @Transactional
    @Override
    public void batchInsertWithExtraInfo(List<NodeRecordWithExtraInfo> nodeRecordWithExtraInfoList) {
        List<NodeRecord> nodeRecords = new ArrayList<>();
        List<NodeRecordExtraInfo> nodeRecordExtraInfoList = new ArrayList<>();
        for (NodeRecordWithExtraInfo nodeRecordWithExtraInfo : nodeRecordWithExtraInfoList) {
            nodeRecords.add(nodeRecordWithExtraInfo);
            nodeRecordExtraInfoList.addAll(nodeRecordWithExtraInfo.getNodeRecordExtraInfoList());
        }
        this.insertBatch(nodeRecords);
        if (nodeRecordExtraInfoList.size() > 0) {
            nodeRecordExtraInfoService.insertBatch(nodeRecordExtraInfoList);
        }
    }
}
