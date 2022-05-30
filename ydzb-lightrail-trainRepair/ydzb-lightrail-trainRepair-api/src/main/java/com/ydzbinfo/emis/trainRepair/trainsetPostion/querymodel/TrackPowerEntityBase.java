package com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel;

import lombok.Data;

import java.util.Date;

/**
 * 股道列位供断电实体(中台实体类)
 * 
 * @author 张天可
 **/
@Data
public class TrackPowerEntityBase {

	private String id;

	/**
	 * 股道编号
	 */
	private String trackCode;
	
	 
	/**
	 * 股道名称
	 */
	private String trackName;
	
	/**
	 * 列位编号
	 */
	private String trackPlaCode;
	 
	/**
	 * 列位名称
	 */
	private String trackPlaName;
	
	/**
	 * 数据来源
	 */
	private String dataSource;
	 
	/**
	 * 运用所编号
	 */
	private String unitCode;
	
	 
	/**
	 * 运用所名称
	 */
	private String unitName;

	/**
	 * 状态 1---有电  2---无电
	 */
	private String state;
	
	/**
	 * 状态切换时间
	 */
	private Date powerChangeTime;

}
