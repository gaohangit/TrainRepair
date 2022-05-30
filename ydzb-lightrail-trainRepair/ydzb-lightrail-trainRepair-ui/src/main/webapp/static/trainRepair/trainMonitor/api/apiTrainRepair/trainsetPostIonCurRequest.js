// 车组位置

/**
 * 根据运用所和股道集合获取车组位置信息
 * unitCode 单位编码 trackCodesJsonStr股道编码 trainsetNameStr车组名称
 */
const getTrainsetPostIon = (params) => {
  return instance.get(
    '/apiTrainRepair/trainsetPostIonCur/getTrainsetPostIon',
    {
      params,
    }
  );
};

// 修改车组位置信息

const setTrainsetPostIon = (data) => {
  return instance.post(
    '/apiTrainRepair/trainsetPostIonCur/setTrainsetPostIon',
    data
  );
};

/**
 * 重联&解编
 *
 */

const setTrainsetState = (data) => {
  return instance.post(
    '/apiTrainRepair/trainsetPostIonCur/setTrainsetState',
    data
  );
};


/**
 * 车组转线
 *
 */

const updTrackCode = (data) => {
  return instance.post(
    '/apiTrainRepair/trainsetPostIonCur/updTrackCode',
    data
  );
};