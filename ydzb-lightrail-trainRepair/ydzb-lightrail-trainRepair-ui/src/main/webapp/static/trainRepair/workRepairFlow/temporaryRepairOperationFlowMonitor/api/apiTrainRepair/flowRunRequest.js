/**
 * 临修作业流程监控
 *
 */
const getTemporaryFlowRunInfoWithTrainsetList = (dayPlanIds,unitCode,trackCodesJsonStr) => {
  return instance({
    url: '/apiTrainRepair/flowRun/getTemporaryFlowRunInfoWithTrainsetList',
    params:{
      dayPlanIds,
      unitCode,
      trackCodesJsonStr
    }
  });
};