package com.ydzbinfo.emis.trainRepair.workprocess.model;
/**
 * 作业任务状态实体(中台实体类)
 * 
 * @author 于雨新
 **/
public class ProcessTaskStateEntity {
	
	private String trainsetID;
	
	/**
	 * 车组名称
	 */
	private String trainsetName;
	
	/**
	 *出所联检状态
	 */
	private String jointInspectionState;

	/**
	 * 故障状态
	 */
	private String faultState;
	
	/**
	 * 检修状态
	 */
	private String repairState;
	

	/**
	 * 运用所编号
	 */
	private String unitCode;
	
	 
	/**
	 * 运用所名称
	 */
	private String unitName;


	public String getTrainsetName() {
		return trainsetName;
	}


	public void setTrainsetName(String trainsetName) {
		this.trainsetName = trainsetName;
	}


	public String getJointInspectionState() {
		return jointInspectionState;
	}


	public void setJointInspectionState(String jointInspectionState) {
		this.jointInspectionState = jointInspectionState;
	}


	public String getFaultState() {
		return faultState;
	}


	public void setFaultState(String faultState) {
		this.faultState = faultState;
	}


	public String getRepairState() {
		return repairState;
	}


	public void setRepairState(String repairState) {
		this.repairState = repairState;
	}


	public String getUnitCode() {
		return unitCode;
	}


	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}


	public String getUnitName() {
		return unitName;
	}


	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}


	public String getTrainsetID() {
		return trainsetID;
	}


	public void setTrainsetID(String trainsetID) {
		this.trainsetID = trainsetID;
	}
	
	
	
}
