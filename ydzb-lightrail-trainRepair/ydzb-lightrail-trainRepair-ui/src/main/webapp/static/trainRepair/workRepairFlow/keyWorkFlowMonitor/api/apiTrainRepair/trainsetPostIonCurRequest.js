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

