// 股道供断电信息

/**
 * 获取显示股道
 * unitCode 单位编码
 */
const getAllTrackArea = (params) => {
  return instance.get(
    '/apiTrainRepair/trackPowerStateCur/getAllTrackArea',
    {
      params,
    }
  );
};

/**
 * 获取股道供断电信息
 * trackCodeList 股道code  unitCodeList单位编码(多个用,间隔)
 */
const getTrackPowerInfo = (params) => {
  return instance.get(
    '/apiTrainRepair/trackPowerStateCur/getTrackPowerInfo',
    {
      params,
    }
  );
};

// 更新股道供断电信息

const setTrackPowerInfo = (data) => {
  return instance.post(
    '/apiTrainRepair/trackPowerStateCur/setTrackPowerInfo',
    data
  );
};
