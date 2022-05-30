package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVectorExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;

import java.util.List;

/**
 * <p>
 * 节点流向额外信息表	 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface INodeVectorExtraInfoService extends IService<NodeVectorExtraInfo> {
    /**
     *删除节点向量额外信息
     */
    void delNodeVectorExtraInfoById(List<NodeVectorWithExtraInfo> nodeVectorWithExtraInfos);
    /**
     *新增节点向量额外信息
     */
    void addNodeVectorExtraInfo(NodeVectorExtraInfo nodeVectorExtraInfo);

}
