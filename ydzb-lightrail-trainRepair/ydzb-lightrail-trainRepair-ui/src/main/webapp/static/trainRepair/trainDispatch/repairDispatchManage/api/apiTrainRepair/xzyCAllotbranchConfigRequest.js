/**
 * 非小组人员配置岗位
 *
 */

const setPostBySon = (data) => {
  return instance.post(
    '/apiTrainRepair/xzyCAllotbranchConfig/setPostBySon',
    data
  );
};


/**
 * 获取当前登录人信息
 *
 */
const getUser = () => {
  return axios({
    url: '/apiTrainRepair/common/getUser',
  });
};