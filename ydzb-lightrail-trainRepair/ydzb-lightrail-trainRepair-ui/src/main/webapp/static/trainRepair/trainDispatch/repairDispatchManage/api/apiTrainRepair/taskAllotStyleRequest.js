/**
 * 获取显示方式
 */
const getTaskAllotStyle = (unitCode,deptCode) => {
  return instance.get(
    '/apiTrainRepair/taskAllotStyle/getTaskAllotStyle',
    {
      params:{
        unitCode,
        deptCode
      },
    }
  );
};

/**
 * 设置显示方式
 */
const setTaskAllotStyle = (unitCode,deptCode,mode) => {
  return instance.post(
    '/apiTrainRepair/taskAllotStyle/setTaskAllotStyle',
      {
        unitCode,
        deptCode,
        mode
      },
  );
};


/**
 * 获取故障
 */
const getFaultData = (trainsetIdList,findFaultTime,dayPlanId) => {
  return instance.post(
    '/apiTrainRepair/xzyMTaskallotpacket/getFaultData',
      {
        trainsetIdList,
        findFaultTime,
        dayPlanId
      },
  );
};