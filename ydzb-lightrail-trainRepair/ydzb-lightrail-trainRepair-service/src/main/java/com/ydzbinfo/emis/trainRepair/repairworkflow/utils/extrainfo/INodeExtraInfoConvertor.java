package com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.NodeInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeExtraInfo;

/**
 * 节点额外信息转换
 *
 * @param <T>
 * @author 张天可
 */
public interface INodeExtraInfoConvertor<T> extends IExtraInfoConvertor<T, NodeInfo, NodeExtraInfo> {

}
