/**
 * 获取运用所编码
 */
const getUnitCode = () => {
  return instance.get('/apiTrainRepair/common/getUnitCode');
};

/**
 * 获取运用所名称
 */
const getUnitName = () => {
  return instance.get('/apiTrainRepair/common/getUnitName');
};

/**
 * 获取出入所车组
 * unitCode 单位编码 37
 */
const getTrainsetListReceived = (params) => {
  return instance.get('/apiTrainRepair/common/getTrainsetNameListRepair', {
    params,
  });
};

/**
 * 获取所有所车组
 * unitCode 单位编码 37
 */
const getTrainsetList = () => {
  return instance.get('/apiTrainRepair/common/getTrainsetList');
};

/**
 * 获取其他车组(热备车)和高级修
 *
 */
const getTrainsetHotSpareInfo = (params) => {
  return instance.get('/apiTrainRepair/common/getTrainsetHotSpareInfoAndHighLevelRepair', {
    params,
  });
};

/**
 * 获取故障状态及数量
 * trainsetid 车组id
 */
const getFaultSearch = (params) => {
  return instance.get('/apiTrainRepair/common/getFaultSearch', {
    params,
  });
};

/**
 * 重联车型选择
 *
 */
const getTrainTypeList = (params) => {
  return instance.get('/apiTrainRepair/common/getTrainTypeList', {
    params,
  });
};

/**
 * 获取选择框作业包集合
 *
 */
const getPacketList = (params) => {
  return instance.get('/apiTrainRepair/monitorPacket/getPacketList', {
    params,
  });
};

/**
 * 获取日计划编号
 *
 */
const getDay = () => {
  return instance.get('/apiTrainRepair/common/getDay', {});
};

/**
 * 根据班次获取工作时间
 *
 */
const getWorkTimeByDayPlanId = (dayPlanId) => {
  return instance.get('/apiTrainRepair/common/getWorkTimeByDayPlanId', {
    params: {
      dayPlanId,
    },
  });
};
/**
 * 获取调车计划
 * @param {*} beginDate
 * @param {*} endDate
 * @param {*} deptCode
 * @param {*} emuId
 */
const getShuntingPlanByCondition = (beginDate, endDate, deptCode, emuId) => {
  return instance.get('/apiTrainRepair/common/getShuntingPlanByCondition', {
    params: {
      beginDate,
      endDate,
      deptCode,
      emuId,
    },
  });
};

/**
 * 获取开行信息
 *
 */
const getRunRoutingDataByDate = (params) => {
  return instance.get('/apiTrainRepair/common/getRunRoutingDataByDate', {
    params,
  });
};

/**
 * 获取车组详情(长编短编)
 *
 */
const getTrainsetDetialInfo = (params) => {
  return instance.get('/apiTrainRepair/common/getTrainsetDetialInfo', {
    params,
  });
};

/**
 *
 * 获取检修列表
 */

const getPgMPacketrecordList = (params) => {
  return instance.get('/apiTrainRepair/monitorPacket/getPgMPacketrecordList', { params });
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

/**
 * 获取故障状态及数量
 *
 */
const getFaultDataByIdList = (params) => {
  return instance.get('/apiTrainRepair/common/getFaultDataByIdList', {
    params,
  });
};

/**
 * 获取id
 *
 */
const getNewId = (params) => {
  return instance.get('/apiTrainRepair/common/getNewId', {
    params,
  });
};


/**
 * 获取角色
 *
 */
const getPostRoleList = (params) => {
  return instance.get('/apiTrainRepair/common/getPostRoleList', {
    params,
  });
};