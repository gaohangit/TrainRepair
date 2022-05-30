package com.ydzbinfo.emis.trainRepair.workProcess.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessTimeRecordWithWorkerInfo;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessTimeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProcessTimeRecordMapper extends BaseMapper<ProcessTimeRecord> {

    List<String> selectPersonId(@Param("workerId") String workerId,
                               @Param("trainsetId") String trainsetId,
                               @Param("repairType")String repairType,
                               @Param("itemCode")String itemCode,
                               @Param("dayplanId") String dayplanId);

    int addTimeRecord(ProcessTimeRecord processTimeRecord);

    /**
     * 根据processID获取当前作业过程所在项目层级的时间记录
     *
     * @param processID 任意作业过程记录id，注意：不是时间记录的id
     * @return
     * @author zhangtk
     */
    List<ProcessTimeRecordWithWorkerInfo> getItemTimeRecordListByProcessID(String processID);

    /**
     * 根据人员表主键集合查询要删除的时间记录表集合
     */
    List<ProcessTimeRecord> getDelTimeRecordList(List<String> personIdList);
}
