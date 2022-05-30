package com.ydzbinfo.emis.trainRepair.bill.model.bill;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.param.WorkerInfo;
import lombok.Data;

import java.util.List;

/**
 * 查询一二级修回填列表返回对象
 *
 * @author 韩旭
 * @since 2021-07-30
 */
@Data
public class QueryChecklistQueryCondition {
    //车组集合
    private List<TrainsetBaseInfo> trainsetList;
    //修程集合
    private List<MainCycWithTypes> mainCycList;
    //派工人员
    private List<WorkerInfo> workAllotPerson;
    //回填状态
    private List<FillState> fillState;
}
