/**
 * 获取所有动态监控配置
 *
 */
const getTrainsetLocationConfigs = (params) => {
  return instance.get(
    '/apiTrainRepair/trainsetLocationConfig/getTrainsetLocationConfigs',
    {
      params,
    }
  );
};

// 修改动态监控配置

const updTrainsetLocationConfig = (data) => {
  return instance.post(
    '/apiTrainRepair/trainsetLocationConfig/updTrainsetLocationConfig',
    data
  );
};


// 新增动态监控配置

const addTrainsetLocationConfigs = (data) => {
  return instance.post(
    '/apiTrainRepair/trainsetLocationConfig/addTrainsetLocationConfigs',
    data
  );
};

// 新增股道监控配置

const addTrackConfig = (data) => {
  return instance.post(
    '/apiTrainRepair/trainsetLocationConfig/addTrackConfig',
    data
  );
};
