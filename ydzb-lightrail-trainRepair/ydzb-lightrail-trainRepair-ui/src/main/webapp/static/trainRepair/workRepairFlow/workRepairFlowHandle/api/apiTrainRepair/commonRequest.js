/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 获取运用所名称
 */
const getUnitName = () => {
  return instance.get('/apiTrainRepair/common/getUnitName');
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
 * 获取角色
 *
 */
const getPostRoleList = (params) => {
  return instance.get('/apiTrainRepair/common/getPostRoleList', {
    params,
  });
};

/**
 * 获取当前登录角色
 *
 */
const getTaskPostList = (unitCode, dayPlanId, staffId, trainsetId) => {
  return instance.get('/apiTrainRepair/common/getTaskPostList', {
    params: {
      unitCode,
      dayPlanId,
      staffId,
      trainsetId,
    },
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
 * 图片上传最大数量
 *
 */
const getPictureUploadMax = (params) => {
  return instance.get('/apiTrainRepair/config/getPictureUploadMax', {
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

