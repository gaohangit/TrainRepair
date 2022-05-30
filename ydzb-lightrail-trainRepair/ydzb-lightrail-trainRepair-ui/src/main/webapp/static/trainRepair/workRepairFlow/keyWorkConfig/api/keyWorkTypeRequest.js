/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
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
 * 查询关键作业类型
 *
 */
const getKeyWorkTypeList = (params) => {
  return instance({
    url: '/apiTrainRepair/keyWorkType/getKeyWorkTypeList',
    params,
  });
};

/**
 * 修改关键作业类型(删除)
 */
const setKeyWorkType = (data) => {
  return axios({
    url: '/apiTrainRepair/keyWorkType/setKeyWorkType',
    method: 'post',
    data,
  });
};
