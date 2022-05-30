/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 获取当前是否为所及
 * true 为段级
 * false 为所级
 */
const isCenter = () => {
  return instance.get('/apiTrainRepair/common/isCenter');
};

/**
 * 获取所有车组
 */
const getTrainsetList = () => {
  return instance.get('/apiTrainRepair/common/getTrainsetList');
};

/**
 * 获取出入所车组
 *
 */
const getTrainsetListReceived = (params) => {
  return instance.get('/apiTrainRepair/common/getTrainsetListReceived', {
    params,
  });
};

/**
 * 获取id
 *
 */
const getNewId = (params) => {
  return instance.get('/apiTrainRepair/common/getNewId', {
    params,
  });
};

/**
 * 获取角色
 *
 */
const getPostRoleList = (params) => {
  return instance.get('/apiTrainRepair/common/getPostRoleList', {
    params,
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
