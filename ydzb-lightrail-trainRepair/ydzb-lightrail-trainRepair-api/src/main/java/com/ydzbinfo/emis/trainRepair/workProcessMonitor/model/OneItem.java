package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/6/2 15:32
 * @Description:
 */
@Data
public class OneItem implements Serializable {

    /**
     * 项目编码
     */
    private String oneItemCode;

    /**
     * 项目名称
     */
    private String oneItemName;

    /**
     * 一级修项目状态 0-未开始 1-已结束（欠时） 2-已超时
     */
    private String oneItemState;

    /**
     * 一级修项目每个辆序的状态
     */
    private List<OneItemCarNoState> oneItemCarNoStateList;

    @Data
    public class OneItemCarNoState{
        //辆序
        private String carNo;
        //状态
        private String carNoState;
    }

}
