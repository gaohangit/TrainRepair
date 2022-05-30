/**
 * 查询关键作业配置
 *
 */
const getKeyWorkConfigList = (params) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkConfigList',
    params,
  });
};

/**
 * 查询关键作业额外列
 */
const getKeyWorkExtraColumnList = () => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnList',
    params:{
      config:true
    }
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
 * 获取当前是否为所及
 * true 为段级
 * false 为所级
 */
const isCenter = () => {
  return instance({
    url: '/apiTrainRepair/common/isCenter',
  });
};

/**
 * 获取所有车型
 */
const getTrainTypeList = () => {
  return instance({
    url: '/apiTrainRepair/common/getTrainTypeList',
  });
};

/**
 * 根据车型获取辆序
 *
 */
const getCarNoListByTrainType = (trainType) => {
  return instance({
    url: '/apiTrainRepair/common/getCarNoListByTrainType',
    params:{
      trainType
    },
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
const getBatchBomNodeCode = (trainsetId,carNo) => {
  return instance({
    url: '/apiTrainRepair/common/getBatchBomNodeCode',
    params:{
      trainsetId,
      carNo
    },
  });
};

/**
 * 修改关键作业配置
 *
 */
const setKeyWorkConfig = (data) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/setKeyWorkConfig',
    method:'post',
    data
  });
};

/**
 * 删除关键作业配置
 *
 */
const delKeyWorkConfig = (data) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/delKeyWorkConfig',
    method:'post',
    data
  });
};
