package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service;


import com.baomidou.mybatisplus.service.IService;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidPosition;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.XzyCRfidRegist;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarCritertion;
import com.ydzbinfo.emis.utils.result.RestResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yyx
 * @since 2020-04-29
 */
public interface IXzyCRfidRegistService extends IService<XzyCRfidRegist> {

	/**
	 * RFID查询
	 * **/
	List<XzyCRfidRegist> QueryXzyCRfidregist(Map<Object, Object> map);

	/**
	 * RFID查询根据TID
	 * **/
	List<XzyCRfidRegist> QueryXzyCRfidregistByTID(Map<Object, Object> map);

	/**
	 * RFID查询 页数量
	 * **/
	String  QueryXzyCRfidregistCount(Map<Object, Object> map);


	/**
	 * RFID注册成功查询
	 * **/
	List<XzyCRfidRegist> QueryXzyCRfidregistOK(Map<Object, Object> map);

	/**
	 * RFID标签注册 判断TID是否注册
	 * **/
	String  isHaveTID(Map<Object, Object> map);

	/**
	 * 批量删除RFID标签（假删除）
	 * **/
	Integer delRfid(List<String> delIdList,String delUserCode,String delUserName);

	/**
	  * @author: 吴跃常
	  * @Description: 获取标签列表信息
	  * @date: 2021-07-28 14:27
	  */
	RestResult selectRfIdList(XzyCRfidRegist model, Integer pageNum, Integer pageSize);

	/**
	 * @author: 吴跃常
	 * @Description: 获取标签位置列表信息
	 * @date: 2021-07-28 14:30
	 */
	RestResult selectRfIdPosition(Integer pageNum, Integer pageSize, String tid, String trackCode, String placeCode, Integer carCount, String unitCode);

	/**
	 * @author: 吴跃常
	 * @Description: 获取标签关系配置列表信息
	 * @date: 2021-07-28 14:30
	 */
	RestResult selectRfIdCriterion(Integer pageNum, Integer pageSize, String itemName, String trainsetSubType, String trainsetType, String repairPlaceCode);

	/**
	 * @author: 吴跃常
	 * @Description: 添加标签位置信息
	 * @date: 2021-07-28 15:17
	 */
	void addPosition(RfidPosition rfidPosition, ShiroUser currentUser);

	/**
	 * @author: 吴跃常
	 * @Description: 添加签标准关系
	 * @date: 2021-07-28 16:00
	 */
	void addCritertion(XzyCRfidcarCritertion cRfidcarCritertion);

	/**
	 * @author: 吴跃常
	 * @Description: 删除标签位置
	 * @date: 2021-07-28 16:00
	 */
	void delPosition(List<String> ids);

	/**
	 * @author: 吴跃常
	 * @Description: 删除标签标准关系
	 * @date: 2021-07-28 16:00
	 */
	void delCritertion(List<String> ids);
}
