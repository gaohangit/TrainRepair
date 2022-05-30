// 获取所有作业包置

const getMonitorPackets = (params) => {
  return instance.get(
    '/apiTrainRepair/monitorPacket/getMonitorPackets',
    {
      params,
    }
  );
};

// 修改作业包

const updMonitorPacket = (data) => {
  return instance.post(
    '/apiTrainRepair/monitorPacket/updMonitorPacket',
    data
  );
};
