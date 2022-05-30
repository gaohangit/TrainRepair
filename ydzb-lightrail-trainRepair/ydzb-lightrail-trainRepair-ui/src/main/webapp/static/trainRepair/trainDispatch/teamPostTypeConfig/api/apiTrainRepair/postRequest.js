/**
 * 获取所有班组岗位
 */
const getPostList = (params) => {
  return instance.get(
    '/apiTrainRepair/post/getPostList',
    {
      params,
    }
  );
};

/**
 * 新增班组岗位
 *
 */

const addPost = (data) => {
  return instance.post(
    '/apiTrainRepair/post/addPost',
    data
  );
};


/**
 * 修改班组岗位
 *
 */

const updPost = (data) => {
  return instance.post(
    '/apiTrainRepair/post/updPost',
    data
  );
};


/**
 * 删除班组岗位
 *
 */

const delPost = (data) => {
  return instance.post(
    '/apiTrainRepair/post/delPost',
    data
  );
};
