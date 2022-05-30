package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/26 15:01
 * @Description: 故障信息
 */
@Data
public class FaultData implements Serializable {

    /**
     * 故障总数
     */
    private Integer faultTotalCount;

    /**
     * 已处理故障数量
     */
    private Integer faultDealCount;

    /**
     * 每个辆序是否有故障的信息集合
     */
    List<FaultState> faultStateList;


    public Integer getFaultDealCount() {
        return faultDealCount==null?0:faultDealCount;
    }

    public void setFaultDealCount(Integer faultDealCount) {
        this.faultDealCount = faultDealCount;
    }

    public Integer getFaultTotalCount() {
        return faultTotalCount==null?0:faultTotalCount;
    }

    public void setFaultTotalCount(Integer faultTotalCount) {
        this.faultTotalCount = faultTotalCount;
    }

    @Data
    public class FaultState{
        private String carNo;
        private String faultState;
    }
}
