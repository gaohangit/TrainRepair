/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 获取日计划编号
 *
 */
const getDay = () => {
  return axios({
    url: '/apiTrainRepair/common/getDay',
  });
};

/**
 * 获取当前登录人信息
 *
 */
const getUser = () => {
  return axios({
    url: '/apiTrainRepair/common/getUser',
  });
};

/**
 * 获取出入所车组
 * unitCode 单位编码 37
 */
const getTrainsetListReceived = (params) => {
  return instance.get('/apiTrainRepair/common/getTrainsetListReceived', {
    params,
  });
};

/**
 * 获取功能分类
 *
 */
const getFunctionClass = () => {
  return instance({
    url: '/apiTrainRepair/common/getFunctionClass',
  });
};

/**
 * 获取故障列表
 *
 */
const getCenterFaultInfo = (params) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkFlowRun/getCenterFaultInfo',
    params,
  });
};

/**
 * 获取故障列表(转关键作业)
 *
 */
const getKeyWorkFlowRunByFault = (data) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkFlowRun/getKeyWorkFlowRunByFault',
    method: 'post',
    data,
  });
};

/**
 * 类型
 *
 */
const getWorkTypeList = () => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnValueList',
    params: {
      columnKey: 'KEY_WORK_TYPE',
    },
  });
};
/**
 * 作业条件
 *
 */
const getWorkEnvList = () => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnValueList',
    params: {
      columnKey: 'WORK_ENV',
    },
  });
};

/**
 * 故障关键作业录入
 *
 */
const setKeyWorkFlowRunByFault = (data) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkFlowRun/setKeyWorkFlowRunByFault',
    method: 'post',
    data,
  });
};

/**
 * 获取班组
 *
 */

const getWorkTeam = (params) => {
  return instance({
    url: '/apiTrainRepair/common/getWorkTeam',
    params,
  });
};

/**
 * 获取可打卡班组
 *
 */
const getActuallyWorkTeams = (params) => {
  return instance.get('/apiTrainRepair/common/getActuallyWorkTeams', {
    params,
  });
};
