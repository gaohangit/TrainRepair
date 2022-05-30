/**
 * 查询流程类型
 */
const getFlowTypeList = (params) => {
  return instance.get(
    '/apiTrainRepair/flowType/getFlowTypeList',
    {
      params,
    }
  );
};


