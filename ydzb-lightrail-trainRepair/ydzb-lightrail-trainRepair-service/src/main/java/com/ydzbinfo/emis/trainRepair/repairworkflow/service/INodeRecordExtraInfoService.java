package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecordExtraInfo;

/**
 * <p>
 * 节点记录额外信息表	比如：图片信息、表单信息等 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface INodeRecordExtraInfoService extends IService<NodeRecordExtraInfo> {
    void addNodeRecordExtraInfo(NodeRecordExtraInfo nodeRecordExtraInfo);
}
