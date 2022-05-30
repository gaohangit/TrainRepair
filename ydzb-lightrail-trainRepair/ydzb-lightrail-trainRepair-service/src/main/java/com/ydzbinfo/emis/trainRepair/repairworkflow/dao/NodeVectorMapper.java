package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVector;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 节点流向表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface NodeVectorMapper extends BaseMapper<NodeVector> {
    List<NodeVectorWithExtraInfo> getNodeVectorWithInfoList(@Param("flowId") String flowId);

}
