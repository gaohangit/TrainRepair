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

