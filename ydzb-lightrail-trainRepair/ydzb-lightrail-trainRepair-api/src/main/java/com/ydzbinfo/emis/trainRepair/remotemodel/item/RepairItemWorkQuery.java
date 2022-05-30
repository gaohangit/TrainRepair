package com.ydzbinfo.emis.trainRepair.remotemodel.item;

import lombok.Data;

import java.util.List;

/**
 * @description: 获取检修项目 查询对象
 * @date: 2021/10/22
 * @author 冯帅
 * @modified 张天可
 */
@Data
public class RepairItemWorkQuery {

    //车型（可能为空）
    String trainType;

    //批次（可能为空）
    String trainSubType;

    //车组ID集合
    List<String> trainSetIdList;

    //检修项目名称（支持模糊查询）
    String itemName;

    //检修项目编码
    String itemCode;

    //段编码
    String depotCode;

    //每页大小
    Integer limit;

    //第几页
    Integer page;

}
