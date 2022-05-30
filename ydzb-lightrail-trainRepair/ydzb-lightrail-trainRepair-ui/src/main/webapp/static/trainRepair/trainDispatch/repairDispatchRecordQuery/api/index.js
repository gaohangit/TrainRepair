/**
 * 获取当前部署等级
 */
const getIsCenter = () => {
  return instance({
    url: '/apiTrainRepair/common/getIsCenter',
  });
};
/**
 * 获取段下运用所
 */
const getUnitList = () => {
  return instance({
    url: '/apiTrainRepair/common/getUnitList',
  });
};



/**
 * 获取所有车组
 */
const getTrainsetList = () => {
  return instance.get('/apiTrainRepair/common/getTrainsetList');
};

/**
 * 获取作业类型
 */
const getItemType = () => {
  return instance({
    url: '/apiTrainRepair/xzyMTaskallotpacket/getItemType',
  });
};

/**
 * 获取单位下的班组
 */
const getWorkTeamsByUnitCode = (unitCode) => {
  return axios({
    url: '/apiTrainRepair/common/getWorkTeamsByUnitCode',
    params: {
      unitCode,
    },
  });
};

/**
 * 获取班组下的作业人员
 */
const getGroup = (deptCode) => {
  return axios({
    url: '/apiTrainRepair/xzyCAllotbranchConfig/getGroup',
    method: 'post',
    data: {
      deptCode,
    },
  });
};

/**
 * 获取班组下的作业人员
 */
const getPersonByDept = (deptCode) => {
  return axios({
    url: '/apiTrainRepair/workProcess/getPersonByDept',
    method: 'get',
    params: {
      deptCode,
    },
  });
};

/**
 * 查询作业包列表
 */
const getTaskAllotDataByDate = (data) => {
  return instance({
    url: '/apiTrainRepairMidGround/xzyMTaskallot/getTaskAllotDataByDate',
    method: 'post',
    data,
  });
};

/**
 * 派工列表查询接口
 */
const getQueryTaskAllotList = (data) => {
  return instance({
    url: '/apiTrainRepair/xzyMTaskallotpacket/getQueryTaskAllotList',
    method: 'post',
    data,
  });
};
