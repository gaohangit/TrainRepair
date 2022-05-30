package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.NodeRecordInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryNodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeRecordWithExtraInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 节点记录表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface INodeRecordService extends IService<NodeRecord> {
    /**
     * 节点记录和节点记录额外信息
     * @return
     */
    List<NodeRecordInfo> getNodeRecordInfoList(QueryNodeRecord queryNodeRecord);

    void insertWithExtraInfo(NodeRecordWithExtraInfo nodeRecordWithExtraInfo);

    @Transactional
    void batchInsertWithExtraInfo(List<NodeRecordWithExtraInfo> nodeRecordWithExtraInfoList);
}
