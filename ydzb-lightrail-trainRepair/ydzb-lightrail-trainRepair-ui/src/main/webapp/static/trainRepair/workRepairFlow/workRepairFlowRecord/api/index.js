/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 获取所有车组
 */
const getTrainsetList = () => {
  return instance.get('/apiTrainRepair/common/getTrainsetList');
};

const getTrainsetByDateAndFlowTypeCode = (params) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getTrainsetByDateAndFlowTypeCode',
    params
  });
};

/**
 * 查询流程类型
 */
const getFlowTypeListByFlowPageCode = (params) => {
  return instance({
    url: '/apiTrainRepair/flowType/getFlowTypeListByFlowPageCode',
    params
  });
};

/**
 * 查询流程
 */
const getTaskFlowConfigList = (params) => {
  return instance({
    url: '/apiTrainRepair/taskFlowConfig/getTaskFlowConfigList',
    params
  });
};

/**
 * 查询流程名称
 */
const getFlowTypes = (params) => {
  return instance({
    url: '/apiTrainRepair/taskFlowConfig/getFlowByFlowPageCodeAndFlowTypeCode',
    params
  });
};

/**
 * 获取单位下的班组
 */
const getWorkTeamsByUnitCode = (unitCode) => {
  return axios({
    url: '/apiTrainRepair/common/getAllTeamsByUnitCode',
    params: {
      unitCode,
    },
  });
};

/**
 * 获取班组下的作业人员
 */
const getPersonByDept = (deptCode) => {
  return axios({
    url: '/apiTrainRepair/workProcess/getPersonByDept',
    method: 'get',
    params: {
      deptCode,
    },
  });
};

/**
 * 作业流程处理记录查询
 */
const getFlowRunRecordList = (params) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getFlowRunRecordList',
    params,
  });
};
