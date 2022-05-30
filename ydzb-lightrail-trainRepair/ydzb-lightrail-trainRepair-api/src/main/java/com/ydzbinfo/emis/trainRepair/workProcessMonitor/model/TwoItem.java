package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/6/1 15:57
 * @Description: 二级修作业项目实体
 */
@Data
public class TwoItem implements Serializable {

    /**
     * 二级修项目所在作业包编码
     */
    private String twoPacketCode;

    /**
     * 二级修项目所在作业包名称
     */
    private String twoPacketName;

    /**
     * 二级修项目编码
     */
    private String twoItemCode;

    /**
     * 二级修项目名称
     */
    private String twoItemName;

    /**
     * 二级修项目辆序
     */
    private List<String> twoItemCarNos;

    /**
     * 二级修项目作业人员
     */
    private List<String> twoItemPerson;

    /**
     * 二级修项目完成状态  0-未完成  1-已完成
     */
    private String twoItemState;
}
