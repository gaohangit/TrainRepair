/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 查询关键作业额外列
 */
const getKeyWorkExtraColumnList = () => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnList',
    params: {},
  });
};

/**
 * 查询关键作业额外列对应值
 *
 */
const getKeyWorkExtraColumnValueList = (params) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnValueList',
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
 * 获取部件构形节点编码
 *
 */
const getBatchBomNodeCode = (trainsetId, carNoListStr) => {
  return instance({
    url: '/apiTrainRepair/common/getBatchBomNodeCode',
    params: {
      trainsetId,
      carNoListStr,
    },
  });
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
const getTrainsetListByTrack = (params) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkFlowRun/getTrainsetListByTrack',
    params,
  });
};

/**
 * 根据车型查询批次列表
 */
const getPatchListByTraintype = (traintype) => {
  return axios({
    url: '/apiTrainResume/resume/getPatchListByTraintype',
    params: {
      traintype,
    },
  });
};

/**
 * 根据车型获取辆序
 *
 */
const getCarNoListByTrainType = (trainType) => {
  return instance({
    url: '/apiTrainRepair/common/getCarNoListByTrainType',
    params: {
      trainType,
    },
  });
};

/**
 * 根据车型查询关键作业配置
 *
 */
const getKeyWorkConfigListByTrainModel = (unitCode, trainModel) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkConfigListByTrainModel',
    params: {
      unitCode,
      trainModel,
    },
  });
};
/**
 * 关键作业录入
 *
 */
const setKeyWorkFlowRun = (data) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkFlowRun/setKeyWorkFlowRun',
    method: 'post',
    data,
  });
};

/**
 * 查询关键作业流程
 *
 */
const getKeyWorkFlowRunList = (params) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkFlowRun/getKeyWorkFlowRunList',
    params,
  });
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
 * 获取关键作业流程
 *
 */

const getFlowTypes = (params) => {
  return instance({
    url: '/apiTrainRepair/taskFlowConfig/getFlowByFlowPageCodeAndFlowTypeCode',
    params,
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
 * 有权限处理节点
 *
 */
const setNodeDispose = (data) => {
  return axios({
    url: '/apiTrainRepair/flowRun/setNodeDispose',
    method: 'post',
    data,
  });
};

/**
 * 无视权限校验进行节点处理
 *
 */
const setNodeDisposeNotCheck = (data) => {
  return axios({
    url: '/apiTrainRepair/flowRun/setNodeDisposeNotCheck',
    method: 'post',
    data,
  });
};

/**
 * 查看流程处理情况-流程图
 *
 */
const getFlowDisposeGraph = (flowRunId) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getFlowDisposeGraph',
    params: {
      flowRunId,
    },
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
 * 上传图片
 *
 */
const uploadedFileInfo = (data) => {
  return instance({
    url: '/apiTrainRepair/flowRun/uploadedFileInfo',
    method: 'post',
    data,
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

/**
 * 撤回当前流程
 *
 */
const revokeKeyWorkFlowRun = (data) => {
  return instance({
    url: '/apiTrainRepair/flowRun/revokeKeyWorkFlowRun',
    method: 'post',
    data,
  });
};
