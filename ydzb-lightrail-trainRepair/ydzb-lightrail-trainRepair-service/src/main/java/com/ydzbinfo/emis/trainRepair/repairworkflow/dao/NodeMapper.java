package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Node;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeWithExtraInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 节点表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface NodeMapper extends BaseMapper<Node> {
    List<NodeWithExtraInfo> getNodeWithExtraInfoList(@Param("flowId") String flowId);

    /**
     * 根据节点查到节点配置
     * @param NodeId
     * @return
     */
    NodeWithExtraInfo getNodeWithExtraInfoByNodeId(@Param("NodeId") String NodeId);

}
