/**
 * 获取当前部署等级
 */
const getIsCenter = () => {
  return instance({
    url: '/apiTrainRepair/common/getIsCenter',
  });
};
/**
 * 获取段下运用所
 */
const getUnitList = () => {
  return instance({
    url: '/apiTrainRepair/common/getUnitList',
  });
};


/**
 * 获取所有车组
 */
const getTrainsetList = () => {
  return instance.get('/apiTrainRepair/common/getTrainsetList');
};
/**
 * 获取单位下的班组
 */
const getWorkTeamsByUnitCode = (unitCode) => {
  return axios({
    url: '/apiTrainRepair/common/getWorkTeamsByUnitCode',
    params: {
      unitCode,
    },
  });
};

/**
 * 获取统计时长
 */
const getDuration = (params) => {
  return axios({
    url: '/apiTrainRepair/processStatistics/duration',
    params,
  });
};


/**
 * 获取统计时长明细
 */
const getDurationDetail = (params) => {
  return axios({
    url: '/apiTrainRepair/processStatistics/durationDetail',
    params,
  });
};

/**
 * 获取作业预警统计分析
 */
const getWarning = (params) => {
  return axios({
    url: '/apiTrainRepair/processStatistics/warning',
    params,
  });
};
