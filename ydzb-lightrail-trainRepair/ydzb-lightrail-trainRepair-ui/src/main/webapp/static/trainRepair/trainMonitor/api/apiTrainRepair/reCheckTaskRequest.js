// const getOverRunRecordList = (pageNum, pageSize, TrainsetNameList) => {
//   return instance({
//     url: '/apiTrainRepair/reCheckTask/getOverRunRecordList',
//     method: 'post',
//     data: {
//       pageNum,
//       pageSize,
//       TrainsetNameList,
//     },
//   });
// };
// ly
const getLyOverRunRecordList = (TrainsetNameList) => {
  return instance({
    url: '/apiTrainRepair/reCheckTask/getLyOverRunRecordList',
    method: 'get',
    params: {
      trainsetNameStr: TrainsetNameList.join(','),
    },
  });
};
// sj
const getSjOverRunRecordList = (TrainsetNameList) => {
  return instance({
    url: '/apiTrainRepair/reCheckTask/getSjOverRunRecordList',
    method: 'get',
    params: {
      trainsetNameStr: TrainsetNameList.join(','),
    },
  });
};
