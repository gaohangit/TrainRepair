// 获取所有调车记事

const getMonitornotecontents = (params) => {
  return instance.get(
    '/apiTrainRepair/monitorNoteContent/getMonitornotecontents',
    {
      params,
    }
  );
};

// 根据id获取调车记事

const getMonitornotecontentById = (params) => {
  return instance.get(
    '/apiTrainRepair/monitorNoteContent/getMonitornotecontentById',
    {
      params,
    }
  );
};

// 新增调车记事

const addMonitornotecontent = (data) => {
  return instance.post(
    '/apiTrainRepair/monitorNoteContent/addMonitornotecontent',
    data
  );
};

// 修改调车记事

const updMonitornotecontent = (data) => {
  return instance.post(
    '/apiTrainRepair/monitorNoteContent/updMonitornotecontent',
    data
  );
};

// 删除调车记事

const delMonitornotecontent = (data) => {
  return instance.post(
    '/apiTrainRepair/monitorNoteContent/delMonitornotecontent',
    data
  );
};
