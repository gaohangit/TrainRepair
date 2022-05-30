/**
 * 根据车组获取流程处理信息
 *
 */
const getFlowRunInfos = (unitCode, dayPlanId, trainsetId, showDayRepairTask) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getFlowRunInfosByTrainMonitor',
    params: {
      flowPageCode: 'TRAIN_MONITOR',
      unitCode,
      dayPlanId,
      trainsetId,
      showDayRepairTask
    },
  });
};

/**
 * 根据车组获取流程处理信息
 *
 */
const getHostLingFlowRunInfos = (unitCode, dayPlanId, trainsetId, showDayRepairTask) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getFlowRunInfosByTrainMonitor',
    params: {
      flowPageCode: 'HOSTLING',
      unitCode,
      dayPlanId,
      trainsetId,
      showDayRepairTask
    },
  });
};

