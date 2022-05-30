package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.trainRepair.workprocess.model.base.BaseProcessData;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/19 10:28
 * @Description: 一级修查询实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryOneWorkProcessData extends BaseProcessData implements Serializable {

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 作业标准主键
     */
    private String critertionId;

    /**
     * 辆序集合
     */
    private List<String> carNos;

    /**
     * 开始时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 记录时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;


    /**
     * 实际图片数量
     */
    private Integer actualPicCount;

    /**
     * 标准图片数量
     */
    private Integer standardPicCount;

    /**
     * 图片集合
     */
    List<ProcessPic> processPicList;

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
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 手持机或PC，手持机1，PC2
     */
    private String dataSource;
}
