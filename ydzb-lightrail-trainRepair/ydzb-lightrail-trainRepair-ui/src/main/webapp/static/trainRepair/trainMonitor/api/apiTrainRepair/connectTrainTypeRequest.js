// 重联车型配置

// 获取所有重联车型配置

const getConnectTrainTypes = (params) => {
  return instance.get(
    '/apiTrainRepair/connectTrainType/getConnectTrainTypes',
    {
      params,
    }
  );
};

// 获取重联车型配置

const getConnectTrainType = (params) => {
  return instance.get(
    '/apiTrainRepair/connectTrainType/getConnectTrainType',
    {
      params,
    }
  );
};

// 新增重联车型配置

const addConnectTrainType = (data) => {
  return instance.post(
    '/apiTrainRepair/connectTrainType/addConnectTrainType',
    data
  );
};

// 修改重联车型配置

const updConnectTrainType = (data) => {
  return instance.post(
    '/apiTrainRepair/connectTrainType/updConnectTrainType',
    data
  );
};

// 删除重联车型配置

const delConnectTrainType = (data) => {
  return instance.post(
    '/apiTrainRepair/connectTrainType/delConnectTrainType',
    data
  );
};
