/**
 * 获取车型
 */
const getTraintypeListLocal = (params) => {
  return instance.get('/apiTrainRepair/common/getTraintypeListLocal', {
    params,
  });
};

/**
 * 获取批次
 */
const getPatchListByTraintype = (params) => {
  return instance.get('/apiTrainRepair/common/getPatchListByTraintype', {
    params,
  });
};

/**
 * 获取辆序
 */
const getCarNoListByTrainType = (params) => {
  return instance.get('/apiTrainRepair/common/getCarNoListByTrainType', {
    params,
  });
};

/**
 * 获取检修项目
 *
 */

const selectRepairItemListByCarNoParam = (platform, trainType, trainBatch, carNoList) => {
  return instance({
    url: '/apiTrainRepair/common/selectRepairItemListByCarNoParam',
    method: 'post',
    data: {
      platform,
      trainType,
      trainBatch,
      carNoList,
    },
  });
};
