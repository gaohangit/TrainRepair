/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 获取出入所车组
 * unitCode 单位编码 37
 */
const getTrainsetListReceived = (params) => {
  return instance.get(
    '/apiTrainRepair/common/getTrainsetListReceived',
    {
      params,
    }
  );
};

/**
 * 获取作业包选择
 */
const getPackets = (params) => {
  return instance.get(
    '/apiTrainRepair/extraFlowTypePacket/getPackets',
    {
      params,
    }
  );
};

/**
 * 获取服务是否是段级系统
 */
const isCenter = (params) => {
  return instance.get(
    '/apiTrainRepair/common/isCenter',
    {
      params,
    }
  );
};