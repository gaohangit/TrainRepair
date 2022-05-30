/**
 * 查询关键作业额外列
 */
const getKeyWorkExtraColumnList = () => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnList',
    params: {},
  });
};

/**
 * 查询关键作业额外列对应值
 *
 */
const getKeyWorkExtraColumnValueList = (params) => {
  return instance({
    url: '/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnValueList',
    params,
  });
};

/**
 * 获取功能分类
 *
 */
const getFunctionClass = () => {
  return instance({
    url: '/apiTrainRepair/common/getFunctionClass',
  });
};

/**
 * 获取部件构形节点编码
 *
 */
const getBatchBomNodeCode = (trainsetId, carNoListStr) => {
  return instance({
    url: '/apiTrainRepair/common/getBatchBomNodeCode',
    params: {
      trainsetId,
      carNoListStr,
    },
  });
};
/**
 * 关键作业流程监控
 *
 */
const getKeyWorkFlowRunInfoWithTrainsetList = (dayPlanIds,unitCode, showForceEndFlowRun) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getKeyWorkFlowRunInfoWithTrainsetList',
    params:{
      dayPlanIds,
      unitCode,
      showForceEndFlowRun
    },
  });
};

// 获取所有关键作业流程
const getTaskFlowConfigList = () => {
  return instance({
    url:'/apiTrainRepair/taskFlowConfig/getTaskFlowConfigList',
    params:{
      flowTypeCode:'PLANLESS_KEY',
    },
  });
};

/**
 * 获取修程任务
 *
 */
const getRepairTask = (params) => {
  return instance.get('/apiTrainRepair/monitorCommon/getRepairTask', {
    params,
  });
};