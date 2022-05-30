package com.ydzbinfo.emis.trainRepair.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 运用所
 */
public class Unit {

	/**
	 * 全称
	 */

	private String unitName;
	/**
	 * 简称
	 */

	private String unitAbbr;

	private String unitCode;

	private List<WorkTeam> WorkTeams = new ArrayList<WorkTeam>();

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitAbbr() {
		return unitAbbr;
	}

	public void setUnitAbbr(String unitAbbr) {
		this.unitAbbr = unitAbbr;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public List<WorkTeam> getWorkTeams() {
		return WorkTeams;
	}

	public void setWorkTeams(List<WorkTeam> workTeams) {
		WorkTeams = workTeams;
	}

}
