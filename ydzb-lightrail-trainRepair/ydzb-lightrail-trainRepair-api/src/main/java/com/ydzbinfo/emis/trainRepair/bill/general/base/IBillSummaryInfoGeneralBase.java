package com.ydzbinfo.emis.trainRepair.bill.general.base;

import java.util.List;

/**
 * 记录单回填基本实体
 *
 * @param <CELL> 记录单单元格信息实体类
 * @param <AREA> 记录单区域实体类
 * @param <T>    额外信息类（需自定义）
 * @author 张天可
 * @since 2021/8/19
 */
public interface IBillSummaryInfoGeneralBase<CELL extends IBillCellInfoGeneral, AREA extends IBillAreaGeneral, T> {
    /**
     * 获取单元格信息列表
     */
    List<CELL> getCells();

    /**
     * 设置单元格信息列表
     */
    void setCells(List<CELL> cells);

    /**
     * 获取区域信息列表
     */
    List<AREA> getAreas();

    /**
     * 设置区域信息列表
     */
    void setAreas(List<AREA> areas);

    /**
     * 获取额外信息
     */
    T getExtraObject();

    /**
     * 设置额外信息
     */
    void setExtraObject(T extraObject);
}
