package com.ydzbinfo.emis.trainRepair.remotemodel.item;

import lombok.Data;

/**
 * @description: 查询检修项目（新项目）返回实体
 * @date: 2021/10/22
 * @author: 冯帅
 */
@Data
public class RepairItemWorkVo {
    //项目id
    String itemId;

    //单位Code
    String unitCode;

    //单位名字
    String unitName;

    //技术平台
    String platForm;

    //车型
    String trainType;

    //批次
    String trainBatch;

    //适用车组名称（多个以逗号分隔）
    String trainSetName;

    //不适用车组（多个以逗号分隔）
    String notTrainSetName;

    //功能分类
    String funcSysName;

    //维修性质
    String mainTainNature;

    //记录单配置状态（-1：全部，0未配置，1已配置）
    String allocatedTempStatus;

    //项目名称
    String itemName;

    //项目编号
    String itemCode;

    //维修卡片编号
    String mainTainCardNo;

    public String getTrainSetName() {
        return trainSetName;
    }

    public void setTrainSetName(String trainSetName) {
        this.trainSetName = trainSetName;
    }

    public String getNotTrainSetName() {
        return notTrainSetName;
    }

    public void setNotTrainSetName(String notTrainSetName) {
        this.notTrainSetName = notTrainSetName;
    }

    public String getTrainsetName() {
        return trainSetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainSetName = trainsetName;
    }

    public String getNotTrainsetName() {
        return notTrainSetName;
    }

    public void setNotTrainsetName(String notTrainsetName) {
        this.notTrainSetName = notTrainsetName;
    }
}
