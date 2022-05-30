/**
 * 获取预警数据列表
 *
 */
const getWorkWorningList = (data) => {
  return instance({
    url: '/apiTrainRepair/warnmanagent/getWorkWorningList',
    method: 'post',
    data,
  });
};

/**
 * 作业过程风险预警确认
 */
const effectWorkWorning = (data) => {
  return instance({
    url: '/apiTrainRepair/warnmanagent/effectWorkWorning',
    method: 'post',
    data,
  });
};

