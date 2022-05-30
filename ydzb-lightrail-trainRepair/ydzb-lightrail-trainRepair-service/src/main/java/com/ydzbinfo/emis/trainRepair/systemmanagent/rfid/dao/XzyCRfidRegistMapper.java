package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.XzyCRfidRegist;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yyx
 * @since 2020-04-29
 */
public interface XzyCRfidRegistMapper extends BaseMapper<XzyCRfidRegist> {
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
	Integer delRfid(@Param("list") List<String> delIdList, @Param("delUserCode") String delUserCode, @Param("delUserName") String delUserName);
}
