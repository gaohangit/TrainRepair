/**
 * 有权限根据车组获取流程处理信息
 *
 */
const getFlowRunInfos = (params) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getFlowRunInfos',
    params,
  });
};

/**
 * 无视权限根据车组获取流程处理信息
 *
 */
const getFlowRunInfosNotCheck = (params) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getFlowRunInfosNotCheck',
    params,
  });
};

/**
 * 有权限处理节点
 *
 */
const setNodeDispose = (data) => {
  return axios({
    url: '/apiTrainRepair/flowRun/setNodeDispose',
    method: 'post',
    data,
  });
};

/**
 * 无视权限校验进行节点处理
 *
 */
const setNodeDisposeNotCheck = (data) => {
  return axios({
    url: '/apiTrainRepair/flowRun/setNodeDisposeNotCheck',
    method: 'post',
    data,
  });
};

/**
 * 查看流程处理情况-流程图
 *
 */
const getFlowDisposeGraph = (flowRunId) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getFlowDisposeGraph',
    params: {
      flowRunId,
    },
  });
};

/**
 * 上传图片
 *
 */
const uploadedFileInfo = (data) => {
  return instance({
    url: '/apiTrainRepair/flowRun/uploadedFileInfo',
    method: 'post',
    data,
  });
};

/**
 * 获取可切换流程
 *
 */
const getSwitchoverFlow = (flowRunId, unitCode, flowId) => {
  return axios({
    url: '/apiTrainRepair/flowRun/getSwitchoverFlow',
    params: {
      flowRunId,
      unitCode,
      flowId
    },
  });
};

/**
 * 获取可切换流程(强制)
 *
 */
const getForceSwitchoverFlow = (flowRunId, unitCode, flowId) => {
  return axios({
    url: '/apiTrainRepair/flowRun/getForceSwitchoverFlow',
    params: {
      flowRunId,
      unitCode,
      flowId
    },
  });
};

/**
 * 切换流程
 *
 */
const setSwitchoverFlow = (flowRun, targetFlowId) => {
  return instance({
    url: '/apiTrainRepair/flowRun/setSwitchoverFlow',
    method: 'post',
    data: {
      flowRun,
      targetFlowId
    },
  });
};

/**
 * 切换流程(强制)
 *
 */
const setForceSwitchoverFlow = (flowRun, targetFlowId) => {
  return instance({
    url: '/apiTrainRepair/flowRun/setForceSwitchoverFlow',
    method: 'post',
    data: {
      flowRun,
      targetFlowId
    },
  });
};

/**
 * 查询详细流程配置
 */
const getFlowInfoById = (params) => {
  return instance.get('/apiTrainRepair/taskFlowConfig/getFlowInfoById', {
    params,
  });
};