/**
 *
 * 获取所有系统配置
 */
const getConfigList = (params) => {
  return instance.get('/apiTrainRepair/config/getConfigList', {
    params,
  });
};


/**
 *
 * 获取系统配置
 */
const getConfig = (params) => {
  return instance.get('/apiTrainRepair/config/getConfig', {
    params,
  });
};
