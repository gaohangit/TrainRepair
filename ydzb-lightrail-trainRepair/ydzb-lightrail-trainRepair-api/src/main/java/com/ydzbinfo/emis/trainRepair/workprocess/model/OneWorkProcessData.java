package com.ydzbinfo.emis.trainRepair.workprocess.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/5/10 10:57
 * @Description: 一级修作业实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OneWorkProcessData extends ProcessData implements Serializable {

    /**
     * 作业时长
     */
    private long totalTime;

    /**
     * 暂停时长
     */
    private long suspendedTime;

    /**
     * 超欠时状态
     */
    private String timeStatus;

    /**
     * 标准图片数量
     */
    private Integer standardPicCount;
}
