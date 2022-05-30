package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/5/25 17:05
 * @Description:
 */
@Data
public class Integration implements Serializable {

    /**
     * 外协任务名称
     */
    private String integrationName;

    /**
     * 外协任务状态
     */
    private String integrationState;
}
