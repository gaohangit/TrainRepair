package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description
 * @createDate 2021/6/19 14:34
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class KeyWorkConfigInfo extends KeyWorkConfig implements IKeyWorkBase {
    /**
     * 功能分类
     */
    private String functionClass;


    /**
     * 部件(构型)节点编码
     */
    private String batchBomNodeCode;

    /**
     * 类型
     */
    private  String keyWorkType;

    /**
     * 辆序
     */
    private List<String> carNoList;

    /**
     * 位置
     */
    private String position;

    /**
     * 作业条件
     */
    private String workEnv;
}
