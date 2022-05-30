/**
 * 流程强制結束
 *
 */
const forceEndFlowRun = (data) => {
  return instance({
    url: '/apiTrainRepair/flowRun/forceEndFlowRun',
    method: 'post',
    data,
  });
};