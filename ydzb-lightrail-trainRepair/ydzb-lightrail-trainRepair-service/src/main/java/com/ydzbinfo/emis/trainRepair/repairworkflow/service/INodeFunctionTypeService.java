package com.ydzbinfo.emis.trainRepair.repairworkflow.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeFunctionType;

import java.util.List;

/**
 * <p>
 * 节点业务类型表(比如某些节点要关联故障处理、故障质检) 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-27
 */
public interface INodeFunctionTypeService extends IService<NodeFunctionType> {
    List<NodeFunctionType> getNodeFunctionTypeList();

}
