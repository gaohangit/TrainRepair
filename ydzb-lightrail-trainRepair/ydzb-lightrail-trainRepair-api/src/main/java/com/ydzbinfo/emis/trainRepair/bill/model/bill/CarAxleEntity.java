package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import lombok.Data;

/**
 * 辆序和车轴
 */
@Data
public class CarAxleEntity {

    /**
     * 辆序编码（01,02,03,04,05,06,07,00）
     */
    private String carNo;

    /**
     * 轴号（1,2,3,4）
     */
    private String axleNo;

    /**
     * 是否允许回填（false时为N/A）
     */
    private boolean allowFill;

    /**
     * 获取是否允许回填
     * @return
     */
    public boolean getAllowFill(){
        return this.allowFill;
    }
}
