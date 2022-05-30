package com.ydzbinfo.emis.trainRepair.repairmanagement.model.worktask;



import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;

import java.util.ArrayList;
import java.util.List;

/**
 * 派工任务实体(中台实体类)
 * 
 * @author 于雨新
 **/
public class DispatchEntity {


	/**
	 * 作业过程车组实体
	 */
	private TrainsetPositionEntity trainsetPositionEntity = new TrainsetPositionEntity();

	/**
	 * 作业包实体
	 */
	private List<XzyMTaskallotpacket> taskAllotPacket = new ArrayList<>();

	/**
	 * 运用所编号
	 */
	private String unitCode;

	/**
	 * 运用所名称
	 */
	private String unitName;

	/**
	 * 日计划ID
	 */
	private String dayPlanID;

	/**
	 * 车组ID
	 */
	private String trainsetId;

	/**
	 * 备注
	 */
	private String remark;

	public TrainsetPositionEntity getTrainsetPositionEntity() {
		return trainsetPositionEntity;
	}

	public List<XzyMTaskallotpacket> getTaskAllotPacket() {
		return taskAllotPacket;
	}

	public void setTrainsetPositionEntity(TrainsetPositionEntity trainsetPositionEntity) {
		this.trainsetPositionEntity = trainsetPositionEntity;
	}

	public void setTaskAllotPacket(List<XzyMTaskallotpacket> taskAllotPacket) {
		this.taskAllotPacket = taskAllotPacket;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDayPlanID() {
		return dayPlanID;
	}

	public void setDayPlanID(String dayPlanID) {
		this.dayPlanID = dayPlanID;
	}

	public String getTrainsetId() {
		return trainsetId;
	}

	public void setTrainsetId(String trainsetId) {
		this.trainsetId = trainsetId;
	}
}
