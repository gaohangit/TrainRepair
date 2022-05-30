/**
 * 根据运用所和股道集合获取车组位置信息
 * unitCode 单位编码 trackCodesJsonStr股道编码 trainsetNameStr车组名称
 */
const getTrainsetAndTrackPowerInfo = (params) => {
  return instance({
    url:'/apiTrainRepair/flowTrainsetPosition/getTrainsetAndTrackPowerInfo',
    methods:'get',
    params
  })
};