package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

import java.util.Set;

/**
 * @author 张天可
 * @description
 * @createDate 2021/4/20 15:17
 **/
@Data
public class TrainConditionValue {
    /**
     * 车型条件
     */
    private Set<String> trainTypes;

    /**
     * 车型条件是否为排除模式
     */
    private Boolean trainTypeExclude;

    /**
     * 批次条件
     */
    private Set<String> trainTemplates;

    /**
     * 批次条件是否为排除模式
     */
    private Boolean trainTemplateExclude;

    /**
     * 车组条件
     */
    private Set<String> trainsetIds;

    /**
     * 车组条件是否为排除模式
     */
    private Boolean trainsetIdExclude;
}
