package com.ydzbinfo.emis.trainRepair.common.model;

import lombok.Data;

/**
 * 作业班组
 * 
 **/
@Data
public class WorkTeam {
	private String teamCode;
	private String teamName;
	// 简称
	private String teamShortName;
	/**
	 * 父部门编码，通常是运用所
	 */
	private String parentDeptCode;
}
