/**
 * 获取复核任务
 *
 */

const getOverRunRecordList = (data) => {
  return instance.post('/apiTrainRepair/reCheckTask/getOverRunRecordList', data);
};

// const getLyOverRunRecordList = (data) => {
//   return instance.post(
//     '/apiTrainRepair/reCheckTask/getLyOverRunRecordList',
//     data
//   );
// };

// const getSjOverRunRecordList = (data) => {
//   return instance.post(
//     '/apiTrainRepair/reCheckTask/getSjOverRunRecordList',
//     data
//   );
// };

/**
 * 获取所有车组
 */
const getTrainsetList = () => {
  return instance.get('/apiTrainRepair/common/getTrainsetList');
};
