/**
 * 查询节点业务类型
 * 
 */
const getNodeTaskFlowConfigList = (params) => {
  return instance.get(
    '/apiTrainRepair/nodeFunctionType/getTaskFlowConfigList',
    {
      params,
    }
  );
};