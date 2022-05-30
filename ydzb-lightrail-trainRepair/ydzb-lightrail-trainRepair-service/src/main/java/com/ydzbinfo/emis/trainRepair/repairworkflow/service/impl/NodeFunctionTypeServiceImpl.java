package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeFunctionTypeMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeFunctionType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeFunctionTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 节点业务类型表(比如某些节点要关联故障处理、故障质检) 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-27
 */
@Service
public class NodeFunctionTypeServiceImpl extends ServiceImpl<NodeFunctionTypeMapper, NodeFunctionType> implements INodeFunctionTypeService {
    @Resource
    NodeFunctionTypeMapper nodeFunctionTypeMapper;

    @Override
    public List<NodeFunctionType> getNodeFunctionTypeList() {
        return nodeFunctionTypeMapper.getNodeFunctionTypeList();
    }
}
