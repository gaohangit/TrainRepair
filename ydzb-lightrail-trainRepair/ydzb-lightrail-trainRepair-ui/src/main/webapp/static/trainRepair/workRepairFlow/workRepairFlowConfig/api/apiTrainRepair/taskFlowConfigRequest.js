/**
 * 查询作业流程配置
 */
const getTaskFlowConfigList = (params) => {
  return instance.get('/apiTrainRepair/taskFlowConfig/getTaskFlowConfigList', {
    params,
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

/**
 * 修改流程配置
 *
 */
const setTaskFlowConfig = (data) => {
  return instance.post('/apiTrainRepair/taskFlowConfig/setTaskFlowConfig', data);
};

/**
 * 删除流程配置
 *
 */

const delTaskFlowConfig = (data) => {
  return instance.post('/apiTrainRepair/taskFlowConfig/delTaskFlowConfig', data);
};
/**
 * 删除流程配置的提示
 *
 */
const delTaskFlowPrompt = (params) => {
  return instance.get('/apiTrainRepair/taskFlowConfig/getDelTaskFlowConfigWarningInfo', {
    params
  })
}

/**
 * 修改为默认流程
 *
 */

const setDefaultFlowConfig = (params) => {
  return instance.get('/apiTrainRepair/taskFlowConfig/setDefaultFlowConfig', { params });
};

/**
 * 修改发布状态
 *
 */

const setFlowUsable = (params) => {
  return instance.get('/apiTrainRepair/taskFlowConfig/setFlowUsable', { params });
};

/**
 * 获取检修类型
 */
const getRepairTypes = (params) => {
  return instance.get('/apiTrainRepair/taskFlowConfig/getRepairTypes', {
    params,
  });
};

/**
 * 校验流程配置 unitcode
 *
 */

const verifyFlowType = (params) => {
  return instance({
    url: '/apiTrainRepair/taskFlowConfig/verifyFlowType',
    methods: 'get',
    params,
  });
};
