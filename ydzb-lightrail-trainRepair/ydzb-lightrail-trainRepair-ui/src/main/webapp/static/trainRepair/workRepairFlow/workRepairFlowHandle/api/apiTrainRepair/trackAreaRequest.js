/**
 * 获取显示股道
 * unitCode 单位编码
 */

const getAllTrackArea = (params) => {
  return instance({
    url:'/apiTrainRepair/trackPowerStateCur/getAllTrackArea',
    methods:'get',
    params
  })
};