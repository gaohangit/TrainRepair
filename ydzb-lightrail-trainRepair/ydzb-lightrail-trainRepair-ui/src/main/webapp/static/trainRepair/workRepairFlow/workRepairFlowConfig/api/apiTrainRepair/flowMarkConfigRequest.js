/**
 * 查询关键字
 * 
 */
const getFlowMarkConfigList = (params) => {
  return instance.get(
    '/apiTrainRepair/flowMarkConfig/getFlowMarkConfigList',
    {
      params,
    }
  );
};

/**
 * 删除关键字
 * 
 */
const delFlowMarkConfigById = (params) => {
  return instance.get(
    '/apiTrainRepair/flowMarkConfig/delFlowMarkConfigById',
    {
      params,
    }
  );
};

/**
 * 新增关键字
 *
 */

const addFlowMarkConfig = (data) => {
  return instance.post('/apiTrainRepair/flowMarkConfig/addFlowMarkConfig', data);
};