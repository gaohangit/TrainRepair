/**
 * 获取全部流程类型和作业包配置
 */
const getFlowTypeAndPacket = (params) => {
  return instance.get(
    '/apiTrainRepair/flowType/getFlowTypeAndPacket',
    {
      params,
    }
  );
};