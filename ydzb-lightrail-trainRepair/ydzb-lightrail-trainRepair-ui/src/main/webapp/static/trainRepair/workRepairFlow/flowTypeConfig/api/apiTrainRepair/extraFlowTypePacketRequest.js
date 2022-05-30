/**
 * 修改额外流程类型作业包
 *
 */

const setExtraFlowTypePacket = (data) => {
  return instance.post(
    '/apiTrainRepair/extraFlowTypePacket/setExtraFlowTypePacket',
    data
  );
};
