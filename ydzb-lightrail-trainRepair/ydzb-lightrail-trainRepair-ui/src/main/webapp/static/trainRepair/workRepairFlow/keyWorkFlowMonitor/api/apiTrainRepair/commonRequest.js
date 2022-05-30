/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 获取出入所车组
 * unitCode 单位编码 37
 */
const getTrainsetListReceived = (params) => {
  return instance.get(
    '/apiTrainRepair/common/getTrainsetListReceived',
    {
      params,
    }
  );
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
 * 获取本单位下股道显示配置
 */
const getKeyWorkMonitorConfigs = () => {
  return instance.get(
    '/apiTrainRepair/keyWorkMonitorConfig/getKeyWorkMonitorConfigs',
  );
};

/**
 * 修改本单位下股道显示配置
 */
const setKeyWorkMonitorConfigs = (data) => {
  return instance.post(
    '/apiTrainRepair/keyWorkMonitorConfig/setKeyWorkMonitorConfigs',
      data,
  );
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

/**
 *
 * 修改系统配置
 */
const updConfig = (data) => {
  return instance.post('/apiTrainRepair/config/updConfig', data);
};
