package com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel;


import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessTaskStateEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 车组位置实体(中台实体类)
 *
 * @author 于雨新
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TrainsetPositionEntity extends TrainsetPositionEntityBase {


  /**
   *
   */
  private String outTrainNo;

  /**
   * 列位供断电实体集合
   */
  private Collection<TrackPowerEntity> TrackPowerEntityList = new ArrayList<TrackPowerEntity>();

  /**
   * 日计划ID
   */
  private String DayPlanID;

  /**
   * 作业过程任务状态
   */
  private ProcessTaskStateEntity processTaskState = new ProcessTaskStateEntity();

  /**
   * 作业包集合
   */
  List<ZtTaskPacketEntity> taskPacketEntityList = new ArrayList<>();

  /**
   * 作业班组
   */
  private String workTeams;

  /**
   * 车头所在列位模式 0—1列位，1—2列位
   */
  private String headDirectionMode;

  /**
   * 车尾所在列位模式 0—1列位，1—2列位
   */
  private String tailDirectionMode;

}
