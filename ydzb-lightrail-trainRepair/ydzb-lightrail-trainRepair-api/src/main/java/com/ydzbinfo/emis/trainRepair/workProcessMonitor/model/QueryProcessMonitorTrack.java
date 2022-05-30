package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/25 17:12
 * @Description: 监控查询实体（到股道）
 */
@Data
public class QueryProcessMonitorTrack {
    /**
     * 运用所编码
     */
    private String unitCode;

    /**
     * 运用所名称
     */
    private String unitName;

    /**
     * 股道编码
     */
    private String trackCode;

    /**
     * 股道名称
     */
    private String trackName;

    /**
     * 排序
     */
    private int sort;

    /**
     * 列位数据集合
     */
    List<QueryProcessMonitorPla> queryProcessMonitorPlaList;
}
