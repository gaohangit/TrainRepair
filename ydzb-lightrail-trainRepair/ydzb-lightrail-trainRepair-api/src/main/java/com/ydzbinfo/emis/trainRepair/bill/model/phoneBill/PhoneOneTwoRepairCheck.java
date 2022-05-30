package com.ydzbinfo.emis.trainRepair.bill.model.phoneBill;

import com.ydzbinfo.emis.trainRepair.bill.model.bill.FillState;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.OneTwoRepairCheckList;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateTypeBase;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import lombok.Data;

import java.util.List;

/**
 * @description: 手持机一二级修记录单查询列表实体
 * @date: 2021/10/27
 * @author: 冯帅
 */
@Data
public class PhoneOneTwoRepairCheck {

    //数据列表
    List<OneTwoRepairCheckList> oneTwoRepairCheckList;

    //查询条件-车组集合
    private List<TrainsetBaseInfo> trainsetList;

    //查询条件-回填状态集合
    private List<FillState> fillStateList;

    //查询条件-编组形式集合
    private List<String> marshallingTypeList;

    //查询条件-单据类型集合
    private List<TemplateTypeBase> templateTypeList;
}
