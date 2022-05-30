package com.ydzbinfo.emis.trainRepair.workprocess.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: 冯帅
 * @Date: 2021/7/2 09:47
 * @Description: 作业过程时间记录实体—中台
 */
@Data
public class ProcessTimeRecordEntity implements Serializable {

    /**
     * 主键
     */
    String processId;

    /**
     * 时间
     */
    Date time;

    /**
     * 项目状态（1-开始  2-暂停 3-继续 4-结束）
     */
    String itemTimeState;
}
