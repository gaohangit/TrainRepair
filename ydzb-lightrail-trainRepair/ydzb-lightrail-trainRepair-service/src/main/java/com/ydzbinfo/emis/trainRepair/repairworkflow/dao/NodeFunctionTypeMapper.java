package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeFunctionType;

import java.util.List;

/**
 * <p>
 * 节点业务类型表(比如某些节点要关联故障处理、故障质检) Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-27
 */
public interface NodeFunctionTypeMapper extends BaseMapper<NodeFunctionType> {
    List<NodeFunctionType> getNodeFunctionTypeList();
}
