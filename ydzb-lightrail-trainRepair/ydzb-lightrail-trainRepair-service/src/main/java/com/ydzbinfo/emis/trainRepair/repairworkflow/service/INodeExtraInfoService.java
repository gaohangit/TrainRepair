package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeWithExtraInfo;

import java.util.List;

/**
 * <p>
 * 节点额外信息表	如：是否可跳过、节点激活聚合条件、预警时间信息、动态节点额外条件、节点路由标识（主、次等等，在程序内使用枚举值定义，并由此在程序中定义并行节点的基础路由方法）等 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface INodeExtraInfoService extends IService<NodeExtraInfo> {
    /**
     * 删除节点额外数据
     */
    void delNodeExtraInfoByNodeId(List<NodeWithExtraInfo> list);
    /**
     * 新增节点
     */
    void addNodeExtraInfo(NodeExtraInfo nodeExtraInfo);

}
