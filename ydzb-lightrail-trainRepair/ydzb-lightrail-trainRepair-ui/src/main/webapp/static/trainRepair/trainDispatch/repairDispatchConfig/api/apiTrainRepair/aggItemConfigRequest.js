/**
 * 二级修派工配置查询
 */
const getAggItemConfigList = (params) => {
  return instance.get(
    '/apiTrainRepair/aggItemConfig/getAggItemConfigList',
    {
      params,
    }
  );
};


/**
 * 删除二级修派工配置
 *
 */

const delAggItemConfig = (data) => {
  return instance.post(
    '/apiTrainRepair/aggItemConfig/delAggItemConfig',
    data
  );
};



/**
 * 修改二级修派工配置
 *
 */

const updAggItemConfig = (data) => {
  return instance.post(
    '/apiTrainRepair/aggItemConfig/updAggItemConfig',
    data
  );
};


/**
 * 新增二级修派工配置
 *
 */

const addAggItemConfig = (data) => {
  return instance.post(
    '/apiTrainRepair/aggItemConfig/addAggItemConfig',
    data
  );
};