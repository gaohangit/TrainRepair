package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryNodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeRecordWithExtraInfo;

import java.util.List;

/**
 * <p>
 * 节点记录表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface NodeRecordMapper extends BaseMapper<NodeRecord> {
    /**
     * 节点记录和节点记录额外信息
     * @param queryNodeRecord
     */
    List<NodeRecordWithExtraInfo> getNodeRecordWithExtraInfo(QueryNodeRecord queryNodeRecord);

}
